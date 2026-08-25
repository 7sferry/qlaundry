package com.ferry.order.gateway.invoice;

import com.ferry.order.core.invoice.pdf.InvoiceHtmlComposer;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultInvoiceHtmlComposer implements InvoiceHtmlComposer{
	private static final String TEMPLATE_NAME = "order-invoice";
	private static final Locale LOCALE = Locale.of("id", "ID");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
			.ofPattern("d MMMM yyyy, HH:mm 'WIB'", LOCALE)
			.withZone(ZoneId.of("Asia/Jakarta"));
	private static final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,##0.00",
			DecimalFormatSymbols.getInstance(LOCALE));

	private final ITemplateEngine templateEngine;

	@Override
	public byte[] compose(OrderDomain order, List<OrderItemDomain> items){
		Context context = new Context(LOCALE);
		context.setVariable("orderNumber", order.orderNumberValue());
		context.setVariable("status", displayName(order.status().name()));
		context.setVariable("createdAt", DATE_FORMATTER.format(order.createdAt()));
		context.setVariable("customerName", order.customerNameValue());
		context.setVariable("customerPhone", order.customerPhoneValue());
		context.setVariable("customerEmail", order.customerEmailValue());
		context.setVariable("customerAddress", order.customerAddressValue());
		context.setVariable("serviceName", order.serviceName());
		context.setVariable("unit", displayName(order.unit().name()));
		context.setVariable("unitPrice", formatMoney(order.unitPrice()));
		context.setVariable("quantity", order.quantity());
		context.setVariable("weightKg", order.weightKg());
		context.setVariable("priority", displayName(order.priority().name()));
		context.setVariable("paymentMethod", displayName(order.paymentMethod().name()));
		context.setVariable("paymentStatus", displayName(order.paymentStatus().name()));
		context.setVariable("subtotal", formatMoney(order.subtotal()));
		context.setVariable("discount", order.discount().value().signum() > 0 ? formatMoney(order.discount()) : null);
		context.setVariable("totalPrice", formatMoney(order.totalPrice()));
		context.setVariable("notes", order.notesValue());
		context.setVariable("staffNotes", order.staffNotesValue());
		context.setVariable("pickupAt", DATE_FORMATTER.format(order.pickupAt()));
		context.setVariable("estimatedDeliveryAt", DATE_FORMATTER.format(order.estimatedDeliveryAt()));
		context.setVariable("completedAt", formatOrNull(order.completedAt()));
		context.setVariable("items", items.stream()
				.map(item -> new InvoiceItem(item.label() != null ? item.label() : displayName(item.type().name()),
						item.quantity()))
				.toList());
		String html = templateEngine.process(TEMPLATE_NAME, context);
		return renderPdf(html);
	}

	private byte[] renderPdf(String html){
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.withHtmlContent(html, "");
			builder.toStream(outputStream);
			builder.run();
			return outputStream.toByteArray();
		}catch(IOException e){
			throw new UncheckedIOException("Failed to render invoice PDF", e);
		}
	}

	private String formatMoney(MoneyDomain money){
		return "Rp " + MONEY_FORMATTER.format(money.value());
	}

	private String formatOrNull(Instant instant){
		return instant == null ? null : DATE_FORMATTER.format(instant);
	}

	private String displayName(String enumName){
		String[] words = enumName.split("_");
		StringBuilder builder = new StringBuilder();
		for(String word : words){
			if(!builder.isEmpty()){
				builder.append(' ');
			}
			builder.append(word.charAt(0)).append(word.substring(1).toLowerCase(LOCALE));
		}
		return builder.toString();
	}

	public record InvoiceItem(String label, int quantity){
	}

}
