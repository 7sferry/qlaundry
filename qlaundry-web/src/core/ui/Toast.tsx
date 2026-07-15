/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useState} from 'react';
import {CheckCircle2, Info, X, XCircle} from 'lucide-react';
import {ToastContext as ToastContext1} from "@/core/ui/toastContext.ts";

type ToastType = 'success' | 'error' | 'info';

interface ToastItem {
	id: string;
	type: ToastType;
	message: string;
}

export interface ToastAPI {
	success: (message: string) => void;
	error: (message: string) => void;
	info: (message: string) => void;
}

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
	const [toasts, setToasts] = useState<ToastItem[]>([]);

	const add = useCallback((type: ToastType, message: string) => {
		const id = `toast-${Date.now()}-${Math.random()}`;
		setToasts((prev) => [...prev, {id, type, message}]);
		setTimeout(() => {
			setToasts((prev) => prev.filter((t) => t.id !== id));
		}, 4500);
	}, []);

	const dismiss = useCallback((id: string) => {
		setToasts((prev) => prev.filter((t) => t.id !== id));
	}, []);

	const api: ToastAPI = {
		success: (msg) => add('success', msg),
		error: (msg) => add('error', msg),
		info: (msg) => add('info', msg),
	};

	const Icon = ({type}: { type: ToastType }) => {
		if (type === 'success') return <CheckCircle2 size={16}/>;
		if (type === 'error') return <XCircle size={16}/>;
		return <Info size={16}/>;
	};

	return (
			<ToastContext1 value={api}>
				{children}
				<div className="toast-container" aria-live="polite">
					{toasts.map((t) => (
							<div key={t.id} className={`toast toast--${t.type}`}>
								<span className="toast__icon"><Icon type={t.type}/></span>
								<span className="toast__message">{t.message}</span>
								<button className="toast__dismiss" onClick={() => dismiss(t.id)} aria-label="Dismiss">
									<X size={14}/>
								</button>
							</div>
					))}
				</div>
			</ToastContext1>
	);
};

