package com.ferry.utils.httpclient;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
class DefaultHttpRequestBuilder implements HttpRequestBuilder{

	static final ObjectMapper OBJECT_MAPPER = JsonMapper.shared();
	private static final String TRACE_ID_HEADER = "X-Trace-Id";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	private static final List<Class<?>> WRAPPERS = List.of(CharSequence.class, Number.class,
			Boolean.class, Character.class, Enum.class);
	public static final String COMMA_MARK = ",";

	private final String method;
	private final String baseUri;
	private final Map<String, String> headers = new LinkedHashMap<>();
	private Record requestBody;
	private Record requestParam;
	private Duration timeout = DEFAULT_TIMEOUT;

	DefaultHttpRequestBuilder(String method, String baseUri){
		this.method = method;
		this.baseUri = baseUri;
	}

	@Override
	public HttpRequestBuilder header(String name, String value){
		headers.put(name, value);
		return this;
	}

	@Override
	public HttpRequestBuilder requestParam(Record params){
		this.requestParam = params;
		return this;
	}

	@Override
	public HttpRequestBuilder requestBody(Record body){
		this.requestBody = body;
		return this;
	}

	@Override
	public HttpRequestBuilder timeout(Duration timeout){
		this.timeout = timeout;
		return this;
	}

	@Override
	public HttpRequestExecutor build(){
		Map<String, String> allHeaders = new LinkedHashMap<>(headers);
		String traceId = allHeaders.computeIfAbsent(TRACE_ID_HEADER, _ -> UUID.randomUUID().toString());
		String uri = requestParam == null ? baseUri : baseUri + queryStringOf(requestParam);
		HttpRequest.BodyPublisher publisher;
		if(requestBody == null){
			publisher = HttpRequest.BodyPublishers.noBody();
		} else {
			allHeaders.putIfAbsent(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
			publisher = HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(requestBody));
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.timeout(timeout)
				.method(method, publisher);
		allHeaders.forEach(builder::header);
		log.debug("sending {} {} [trace={}]", method, uri, traceId);
		return new DefaultHttpRequestExecutor(builder.build(), traceId);
	}

	private String queryStringOf(Record record){
		StringJoiner query = new StringJoiner("&", "?", "").setEmptyValue("");
		for(RecordComponent component : record.getClass().getRecordComponents()){
			String value = valueOf(component, record);
			if(value == null){
				continue;
			}
			query.add(URLEncoder.encode(component.getName(), StandardCharsets.UTF_8) + '='
					+ URLEncoder.encode(value, StandardCharsets.UTF_8));
		}
		return query.toString();
	}

	private boolean isWrapperType(Object value){
		return WRAPPERS.stream().anyMatch(allowedClass -> allowedClass.isAssignableFrom(value.getClass()));
	}

	private String valueOf(RecordComponent component, Record queryParameter){
		try{
			Object value = component.getAccessor().invoke(queryParameter);
			if(value == null){
				Class<?> returnType = component.getType();
				if(returnType.isArray() || Collection.class.isAssignableFrom(returnType)){
					return "";
				}
				return null;
			}
			if(isWrapperType(value)){
				return value.toString();
			}
			if(value instanceof Collection<?> collection){
				return collection.stream()
						.filter(Objects::nonNull).filter(this::isWrapperType).map(Object::toString)
						.collect(Collectors.joining(COMMA_MARK));
			}
			if(value instanceof Object[] arrays){
				return Arrays.stream(arrays)
						.filter(Objects::nonNull).filter(this::isWrapperType).map(Object::toString)
						.collect(Collectors.joining(COMMA_MARK));
			}
			if(value.getClass().isArray()){
				return IntStream.range(0, Array.getLength(value)).mapToObj(i -> Array.get(value, i))
						.filter(Objects::nonNull).filter(this::isWrapperType).map(Object::toString)
						.collect(Collectors.joining(COMMA_MARK));
			}
			return null;
		} catch(ReflectiveOperationException e){
			throw new IllegalStateException("Failed to read record component " + component.getName(), e);
		}
	}

}
