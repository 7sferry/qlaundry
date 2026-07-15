/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

interface FieldProps {
	label?: string;
	htmlFor?: string;
	hint?: string;
	children: React.ReactNode;
}

export const Field: React.FC<FieldProps> = ({label, htmlFor, hint, children}) => (
		<div className="field">
			{label && (
					<label className="field__label" htmlFor={htmlFor}>
						{label}
					</label>
			)}
			{children}
			{hint && <span className="muted" style={{fontSize: 12}}>{hint}</span>}
		</div>
);

export const Input: React.FC<React.InputHTMLAttributes<HTMLInputElement>> = ({
	                                                                             className = '',
	                                                                             ...rest
                                                                             }) => <input
		className={`input ${className}`.trim()} {...rest} />;

export const Select: React.FC<React.SelectHTMLAttributes<HTMLSelectElement>> = ({
	                                                                                className = '',
	                                                                                children,
	                                                                                ...rest
                                                                                }) => (
		<select className={`select ${className}`.trim()} {...rest}>
			{children}
		</select>
);

export const Textarea: React.FC<React.TextareaHTMLAttributes<HTMLTextAreaElement>> = ({
	                                                                                      className = '',
	                                                                                      ...rest
                                                                                      }) => <textarea
		className={`textarea ${className}`.trim()} {...rest} />;
