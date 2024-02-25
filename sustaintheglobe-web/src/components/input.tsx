/* eslint-disable @typescript-eslint/no-explicit-any */
export type EcoInputProps = {
    placeholder?: string,
    leftIcon?: any,
    required?: boolean,
    type: string,
    value: any,
    handleChange: (val: string) => void,
    extraStyle?: string,
    onClick?: () => void,
    maxLength?: number;
    minLength?: number;
}
const EcoInput = ({
    placeholder,
    leftIcon,
    required,
    type,
    value,
    handleChange,
    extraStyle,
    onClick,
    maxLength,
    minLength
}: EcoInputProps) => {

    return (
        <div onClick={onClick} className={"flex h-[50px] min-w-[150px] w-full items-center justify-start border-[.2px] shadow-lg px-4 rounded-xl my-4 mx-auto " + extraStyle}>
            {leftIcon ? leftIcon : null}
            <input
                placeholder={placeholder}
                value={value}
                maxLength={maxLength}
                minLength={minLength}
                type={type ?? 'text'}
                required={required}
                className="p-2 text-sm bg-white mt-0 w-full outline-none"
                onChange={(ev) => handleChange(ev.target.value)}
            />
        </div>
    )
}

export default EcoInput