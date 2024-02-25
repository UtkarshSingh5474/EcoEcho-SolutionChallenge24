/* eslint-disable @typescript-eslint/no-explicit-any */
import { ArrowRight, MapPinned, UserCircle2Icon, UserRound, VenetianMask } from 'lucide-react'
import profileImage from '../../assets/icons/ic_default_profile.svg'
import editImage from '../../assets/icons/ic_edit.svg'
import manImage from '../../assets/icons/ic_male.svg'
import womenImage from '../../assets/icons/ic_female.svg'
import otherImage from '../../assets/icons/ic_other.svg'
import EcoInput from '../../components/input'
import { useState } from 'react'
import TextField from '@mui/material/TextField'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { toast } from 'react-toastify'
import { updateUserInfo } from './onboarding.actions'
import ScreenLoader from '../../components/screen-loader'
import { useLoaderStore } from '../../store/loader.store'
import { useUserStore } from '../../store/user.store'
import { GeoPoint } from 'firebase/firestore'
import { UserProps } from '../../types/user.types'

const OnboardingPage = () => {
    const { userId } = useParams()
    const navigate = useNavigate()
    const { saveUser } = useUserStore()
    const [extras, setExtras] = useState({})
    const { enableLoader, disableLoader } = useLoaderStore()
    const [formData, setFormData] = useState<any>({
        username: '',
        bio: '',
        gender: '',
        location: ''
    })

    const handleSubmit = () => {
        if (userId) {
            enableLoader()
            const dt = {
                ...formData,
                ...extras,
            }
            delete dt['password']
            updateUserInfo(userId, dt, (success: boolean, data?: UserProps) => {
                if (success && data) {
                    toast('Profile Updated!')
                    saveUser(data)
                    localStorage.setItem('USER_ID', userId)
                    navigate('/tasks')
                }
                disableLoader()
            })
        }
    }

    const handleGetLocation = () => {
        try {
            enableLoader()
            navigator.geolocation.getCurrentPosition((val) => {
                const { latitude, longitude } = val.coords
                fetch(`https://us1.locationiq.com/v1/reverse?key=${import.meta.env.VITE_SOME_KEY}&lat=${latitude}&lon=${longitude}&format=json&`).then(res => res.json()).then(res => {
                    setFormData({ ...formData, location: `${res.address.city},${res.address.state},${res.address.country}` })
                    setExtras({
                        cityName: res.address.city,
                        countryName: res.address.country,
                        location: new GeoPoint(latitude, longitude)
                    })
                })
                toast('Got your location!')
                disableLoader()
            })
        } catch (err) {
            disableLoader()
            toast('Error getting location!')
        }
    }

    if (!userId) {
        return <ScreenLoader />
    }
    return (
        <div className="flex flex-col items-center min-h-[400px] p-4">
            <div className='relative p-5'>
                <img src={profileImage} width={100} height={100} />
                <img src={editImage} className=' absolute bottom-4 -right-1' width={60} height={60} />
            </div>
            <h1 className='text-3xl font-bold text-purple-800'>About You</h1>

            <form onSubmit={(e) => {
                e.preventDefault();
                handleSubmit()
            }} className='w-full'>
                <EcoInput
                    placeholder='Username'
                    type='text'
                    leftIcon={<UserCircle2Icon color='black' />}
                    required={true}
                    value={formData.username}
                    handleChange={(val) => setFormData({ ...formData, username: val })}
                />

                <div className='p-4 bg-white rounded-lg border-2 w-full shadow-lg mx-auto'>
                    <div className='flex items-center justify-center'>
                        <UserRound className='mr-2' />
                        <h4 className='text-lg'> Tell us about yourself</h4>
                    </div>
                    <TextField
                        type={'text'}
                        required={true}
                        placeholder={'I would like to help by....'}
                        style={{
                            padding: '10px',
                            width: '100%',
                            margin: '10px auto'
                        }}
                        onChange={(ev) => setFormData({ ...formData, bio: ev.target.value })}
                        variant="standard"
                    />
                </div>

                <div className='p-4 bg-white mt-5 rounded-lg border-2 w-full shadow-lg mx-auto'>
                    <div className='flex items-center justify-center'>
                        <VenetianMask className='mr-2' />
                        <h4 className='text-lg'> Gender</h4>
                    </div>
                    <div className='flex items-center justify-around my-3'>
                        <div onClick={() => {
                            setFormData({ ...formData, gender: 'male' })
                        }} className={` border-2 flex items-center justify-center border-gray-500 rounded-xl w-[80px] h-[80px] cursor-pointer ${formData.gender === 'male' ? 'bg-green-200' : 'bg-white'}`}>
                            <img className='w-3/4' src={manImage} />
                        </div>
                        <div onClick={() => {
                            setFormData({ ...formData, gender: 'female' })
                        }} className={` border-2 flex items-center justify-center border-gray-500 rounded-xl w-[80px] h-[80px] cursor-pointer ${formData.gender === 'female' ? 'bg-green-200' : 'bg-white'}`}>
                            <img className='w-3/4' src={womenImage} />
                        </div>
                        <div onClick={() => {
                            setFormData({ ...formData, gender: 'other' })
                        }} className={` border-2 flex items-center justify-center border-gray-500 rounded-xl w-[80px] h-[80px] cursor-pointer ${formData.gender === 'other' ? 'bg-green-200' : 'bg-white'}`}>
                            <img className='w-3/4' src={otherImage} />
                        </div>
                    </div>
                </div>

                <EcoInput
                    onClick={handleGetLocation}
                    placeholder='Tap to get location'
                    type='text'
                    leftIcon={<MapPinned color='black' />}
                    required={true}
                    value={formData?.location}
                    handleChange={(val) => setFormData({ ...formData, username: val })}
                />

                <div className='flex items-center justify-end w-10/12 py-4'>
                    <h2 className='text-xl font-semibold text-left mr-2  text-purple-600'>Continue</h2>
                    <button type='submit' className='w-[100] h-[100] bg-green-500 p-4 rounded-full' >
                        <ArrowRight color='white' />
                    </button>
                </div>

            </form>

            <Link to={'/'}>
                <div className='flex justify-end my-4 pb-6'>
                    <h4 className={`font-semibold text-center text-lg text-purple-600`}>
                        Not your profile?<br />
                        Sign out
                    </h4>
                </div>
            </Link>
        </div>
    )
}

export default OnboardingPage