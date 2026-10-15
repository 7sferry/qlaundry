/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export const ORDER_ENDPOINTS = {
	list: '/order/list',
	detail: '/order/detail',
	create: '/order/create',
	confirm: '/order/confirm',
	pickup: '/order/pickup',
	process: '/order/process',
	ready: '/order/ready',
	deliver: '/order/deliver',
	complete: '/order/complete',
	cancel: '/order/cancel',
	invoiceLink: '/invoice/link',
	invoicePdf: '/public/invoice/pdf',
	serviceList: '/service/list',
} as const;
