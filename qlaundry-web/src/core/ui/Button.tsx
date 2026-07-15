/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

type Variant = 'primary' | 'ghost' | 'subtle' | 'danger';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	variant?: Variant;
	block?: boolean;
	iconOnly?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
	                                              variant = 'primary',
	                                              block = false,
	                                              iconOnly = false,
	                                              className = '',
	                                              children,
	                                              ...rest
                                              }) => {
	const classes = [
		'btn',
		`btn--${variant}`,
		block ? 'btn--block' : '',
		iconOnly ? 'btn--icon' : '',
		className,
	]
			.filter(Boolean)
			.join(' ');

	return (
			<button className={classes} {...rest}>
				{children}
			</button>
	);
};
