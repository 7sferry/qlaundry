/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

export type BadgeTone = 'success' | 'warning' | 'info' | 'neutral';

interface BadgeProps {
	tone?: BadgeTone;
	children: React.ReactNode;
}

export const Badge: React.FC<BadgeProps> = ({tone = 'neutral', children}) => (
		<span className={`badge badge--${tone}`}>{children}</span>
);
