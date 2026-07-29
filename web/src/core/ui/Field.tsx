/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {Eye, EyeOff} from 'lucide-react';

interface FieldProps {
	label?: string;
	htmlFor?: string;
	hint?: string;
	error?: string;
	children: React.ReactNode;
}

export const Field: React.FC<FieldProps> = ({label, htmlFor, hint, error, children}) => (
		<div className={`field${error ? ' field--error' : ''}`}>
			{label && (
					<label className="field__label" htmlFor={htmlFor}>
						{label}
					</label>
			)}
			{children}
			{error
					? <span className="field__error" role="alert">{error}</span>
					: hint && <span className="muted" style={{fontSize: 12}}>{hint}</span>}
		</div>
);

export const Input: React.FC<React.InputHTMLAttributes<HTMLInputElement>> = ({
	                                                                             className = '',
	                                                                             ...rest
                                                                             }) => <input
		className={`input ${className}`.trim()} {...rest} />;

export const PasswordInput: React.FC<React.InputHTMLAttributes<HTMLInputElement>> = ({
	                                                                                     className = '',
	                                                                                     ...rest
                                                                                     }) => {
	const [visible, setVisible] = useState(false);
	return (
			<>
				<input
						className={`input password-input ${className}`.trim()}
						type={visible ? 'text' : 'password'}
						{...rest}
				/>
				<button
						type="button"
						className="password-toggle"
						tabIndex={-1}
						onClick={() => setVisible((prev) => !prev)}
						aria-label={visible ? 'Hide password' : 'Show password'}
				>
					{visible ? <EyeOff size={15}/> : <Eye size={15}/>}
				</button>
			</>
	);
};

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
