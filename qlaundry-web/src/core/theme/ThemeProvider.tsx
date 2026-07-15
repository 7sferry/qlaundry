/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useEffect, useMemo, useState} from 'react';
import type {ThemeContextType, ThemeMode} from './themeContext';
import {ThemeContext} from './themeContext';

const STORAGE_KEY = 'qlaundry.theme';

function getInitialTheme(): ThemeMode {
	const stored = localStorage.getItem(STORAGE_KEY) as ThemeMode | null;
	if (stored === 'light' || stored === 'dark') return stored;
	return 'dark';
}

const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
	const [theme, setThemeState] = useState<ThemeMode>(getInitialTheme);

	useEffect(() => {
		document.documentElement.setAttribute('data-theme', theme);
		localStorage.setItem(STORAGE_KEY, theme);
	}, [theme]);

	const value = useMemo<ThemeContextType>(
			() => ({
				theme,
				setTheme: setThemeState,
				toggleTheme: () => setThemeState((prev) => (prev === 'dark' ? 'light' : 'dark')),
			}),
			[theme],
	);

	return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
};

export default ThemeProvider;
