
/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import React from 'react';
import {Button} from './Button';
import {Modal} from './Modal';

interface ConfirmDialogProps {
	open: boolean;
	title: string;
	message: React.ReactNode;
	confirmLabel: string;
	cancelLabel?: string;
	busy?: boolean;
	onConfirm: () => void;
	onCancel: () => void;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
	                                                            open,
	                                                            title,
	                                                            message,
	                                                            confirmLabel,
	                                                            cancelLabel = 'Cancel',
	                                                            busy = false,
	                                                            onConfirm,
	                                                            onCancel,
                                                            }) => (
		<Modal open={open} onClose={onCancel} title={title} size="sm">
			<p className="muted" style={{fontSize: 13}}>{message}</p>
			<div style={{display: 'flex', gap: 12, justifyContent: 'flex-end', marginTop: 20}}>
				<Button type="button" variant="ghost" disabled={busy} onClick={onCancel}>{cancelLabel}</Button>
				<Button type="button" variant="danger" disabled={busy} onClick={onConfirm}>{confirmLabel}</Button>
			</div>
		</Modal>
);
