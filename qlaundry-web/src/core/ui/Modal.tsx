/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useEffect} from 'react';
import {createPortal} from 'react-dom';
import {X} from 'lucide-react';

interface ModalProps {
	open: boolean;
	onClose: () => void;
	title?: string;
	size?: 'sm' | 'md' | 'lg';
	children: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({open, onClose, title, size = 'md', children}) => {
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

	if (!open) return null;

	return createPortal(
			<div className="modal-backdrop" onClick={onClose} role="dialog" aria-modal>
				<div className={`modal modal--${size}`} onClick={(e) => e.stopPropagation()}>
					{title && (
							<div className="modal__header">
								<h3 className="modal__title">{title}</h3>
								<button className="icon-btn" onClick={onClose} aria-label="Close">
									<X size={18}/>
								</button>
							</div>
					)}
					<div className="modal__body">{children}</div>
				</div>
			</div>,
			document.body,
	);
};
