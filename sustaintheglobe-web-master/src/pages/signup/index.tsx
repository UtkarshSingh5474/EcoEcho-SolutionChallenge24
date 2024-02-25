/* eslint-disable @typescript-eslint/no-explicit-any */
import appImage from '../../assets/icons/login.svg';
import googleImage from '../../assets/icons/google.png';
import fbImage from '../../assets/icons/facebook.png';
import { ArrowRight } from 'lucide-react';
import { config } from './config';
import { toast } from 'react-toastify';
import { Link, useNavigate } from 'react-router-dom';
import EcoInput from '../../components/input';
import { useState } from 'react';
import { createUser } from './signup.actions';
import { useLoaderStore } from '../../store/loader.store';
import moment from 'moment';



export default function SignupPage() {
    const [formData, setFormData] = useState<any>({})
    const navigate = useNavigate();
    const { enableLoader, disableLoader } = useLoaderStore()
    const handleChange = (key: string, value: string | number) => {
        setFormData({ ...formData, [key]: value })
    }

    const handleSubmit = () => {

        if (moment(formData.dob).isAfter(moment())) {
            toast('DOB should not be in future');
            return;
        }
        enableLoader()
        createUser(formData, (_id?: string) => {
            if (_id) {
                toast('Please provide some more info')
                navigate(`/${_id}/onboarding`)
            }
            disableLoader()
        })
    }
    return <div className='flex  items-center flex-col justify-center my-auto '>
        <img src={appImage} width={200} height={200} />
        <div className="flex w-full flex-col items-center min-h-[400px] p-4">
            <h2 className='text-xl font-semibold text-left w-11/12 text-green-500'>Register</h2>
            <div className='flex items-center justify-around my-3'>
                <div onClick={() => toast('Coming soon!')} className='p-3 border-[2px] rounded-lg px-7 mx-2 hover:bg-green-200 transition-all cursor-pointer border-green-500'>
                    <img src={googleImage} width={23} height={23} />
                </div>
                <div onClick={() => toast('Coming soon!')} className='p-3 border-[2px] rounded-lg px-7 mx-2 hover:bg-green-200 transition-all cursor-pointer border-green-500'>
                    <img src={fbImage} width={23} height={23} />
                </div>
            </div>
            <h4 className={`font-semibold text-md text-black text-center mt-4`}>or register with email</h4>

            <form onSubmit={(e) => {
                e.preventDefault();
                handleSubmit()
            }} className='w-full mt-4 items-between justify-between'>
                {
                    config.map((item: any) => {
                        return <EcoInput
                            type={item.type}
                            required={true}
                            maxLength={item.maxLength}
                            minLength={item.minLength}
                            value={formData[item.dataKey]}
                            placeholder={item.placeholder}
                            leftIcon={item.icon}
                            handleChange={(val) => {
                                handleChange(item.dataKey, val)
                            }}
                        />
                    })
                }

                <div className='flex items-center justify-end w-full'>
                    <h2 className='text-md font-semibold text-left mr-2  text-green-500'>Register</h2>
                    <button type='submit' className='w-[60] h-[60] bg-green-500 p-2 rounded-full' >
                        <ArrowRight color='white' />
                    </button>
                </div>

            </form>

            <Link to={'/'}>
                <div className='flex justify-end my-4 pb-6'>
                    <h4 className={`font-semibold text-center text-md text-green-500`}>
                        Have an account already?<br />
                        Login Here
                    </h4>
                </div>
            </Link>
        </div>
    </div >;
}