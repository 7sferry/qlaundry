/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useContext} from "react";
import type {ToastAPI} from "@/core/ui/Toast.tsx";
import {ToastContext} from "@/core/ui/toastContext.ts";

export const useToast = (): ToastAPI => {
	const ctx = useContext(ToastContext);
	if (!ctx) throw new Error('useToast must be used within ToastProvider');
	return ctx;
};