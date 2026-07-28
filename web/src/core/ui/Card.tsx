/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

interface CardProps {
	title?: string;
	subtitle?: string;
	actions?: React.ReactNode;
	className?: string;
	style?: React.CSSProperties;
	children: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({title, subtitle, actions, className = '', style, children}) => (
		<section className={`card ${className}`.trim()} style={style}>
			{(title || actions) && (
					<div className="row row--between">
						<div>
							{title && <h3 className="card__title">{title}</h3>}
							{subtitle && <p className="card__subtitle" style={{marginBottom: 0}}>{subtitle}</p>}
						</div>
						{actions}
					</div>
			)}
			<div className={title ? 'mt-16' : ''}>{children}</div>
		</section>
);
