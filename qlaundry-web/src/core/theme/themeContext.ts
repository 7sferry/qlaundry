/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {createContext} from 'react';

export type ThemeMode = 'light' | 'dark';

export interface ThemeContextType {
	theme: ThemeMode;
	toggleTheme: () => void;
	setTheme: (mode: ThemeMode) => void;
}

export const ThemeContext = createContext<ThemeContextType | undefined>(undefined);
