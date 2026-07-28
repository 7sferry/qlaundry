/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {createContext} from "react";
import type {ToastAPI} from "@/core/ui/Toast.tsx";

export const ToastContext = createContext<ToastAPI | undefined>(undefined);