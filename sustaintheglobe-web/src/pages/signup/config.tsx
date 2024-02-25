import { AtSignIcon, CakeIcon, LockIcon, PhoneCallIcon, User2Icon } from "lucide-react";

export const config = [
    {
        type: 'text',
        placeholder: 'Display Name',
        dataKey: 'fullName',
        icon: <User2Icon color='black' className="my-2" />
    },
    {
        type: 'tel',
        placeholder: 'Phone Number',
        dataKey: 'phone',
        maxLength: 10,
        minLength: 10,
        icon: <PhoneCallIcon color='black' className="my-2" />
    },
    {
        type: 'date',
        placeholder: 'Date of Birth',
        dataKey: 'dob',
        icon: <CakeIcon color='black' className="my-2" />
    },
    {
        type: 'email',
        placeholder: 'Email Address',
        dataKey: 'email',
        icon: <AtSignIcon color='black' className="my-2" />
    },
    {
        type: 'password',
        placeholder: 'Password',
        dataKey: 'password',
        icon: <LockIcon color='black' className="my-2" />
    }
];
