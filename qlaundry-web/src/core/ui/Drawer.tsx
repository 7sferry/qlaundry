/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useEffect} from 'react';
import {createPortal} from 'react-dom';
import {X} from 'lucide-react';

interface DrawerProps {
	open: boolean;
	onClose: () => void;
	title?: string;
	subtitle?: string;
	width?: string;
	children: React.ReactNode;
	footer?: React.ReactNode;
}

export const Drawer: React.FC<DrawerProps> = ({
	                                              open,
	                                              onClose,
	                                              title,
	                                              subtitle,
	                                              width = '480px',
	                                              children,
	                                              footer,
                                              }) => {
	useEffect(() => {
		if (!open) return;
		const handler = (e: KeyboardEvent) => {
			if (e.key === 'Escape') onClose();
		};
		document.addEventListener('keydown', handler);
		document.body.style.overflow = 'hidden';
		return () => {
			document.removeEventListener('keydown', handler);
			document.body.style.overflow = '';
		};
	}, [open, onClose]);

	return createPortal(
			<div className={`drawer-overlay ${open ? 'drawer-overlay--open' : ''}`} onClick={onClose}>
				<div
						className={`drawer ${open ? 'drawer--open' : ''}`}
						style={{width}}
						onClick={(e) => e.stopPropagation()}
				>
					<div className="drawer__header">
						<div>
							{title && <h3 className="drawer__title">{title}</h3>}
							{subtitle && <p className="drawer__subtitle">{subtitle}</p>}
						</div>
						<button className="icon-btn" onClick={onClose} aria-label="Close">
							<X size={18}/>
						</button>
					</div>
					<div className="drawer__body">{children}</div>
					{footer && <div className="drawer__footer">{footer}</div>}
				</div>
			</div>,
			document.body,
	);
};
