/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useContext} from 'react';
import type {ThemeContextType} from './themeContext';
import {ThemeContext} from './themeContext';

export const useTheme = (): ThemeContextType => {
	const context = useContext(ThemeContext);
	if (!context) throw new Error('useTheme must be used within a ThemeProvider');
	return context;
};
