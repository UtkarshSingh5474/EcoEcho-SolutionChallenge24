/* eslint-disable @typescript-eslint/no-explicit-any */

import BottomNavbar from '../../components/bottom-nav'
import { ArrowLeft } from 'lucide-react';
import checkImage from '../../assets/icons/ic_tick.svg'
import add from '../../assets/icons/add.svg'
import { useNavigate, useParams } from 'react-router-dom';
import { createPost } from '../../helpers/post-helper';
import { useUserStore } from '../../store/user.store';
import { useRef, useState } from 'react';
import { toast } from 'react-toastify';
import { useLoaderStore } from '../../store/loader.store';
import EcoInput from '../../components/input';

const AddPost = () => {
    const [file, setFile] = useState<any>();
    const ref = useRef();
    const { enableLoader, disableLoader } = useLoaderStore();
    const imageRef = useRef();
    const [caption, setCaption] = useState('');
    const navigate = useNavigate();
    const { taskId } = useParams();
    const { user } = useUserStore();


    const handleSubmit = async () => {

        if (!file) {
            toast('Please attach a image!');
            return;
        }
        if (taskId) {
            enableLoader()
            await createPost(taskId, {
                userId: user?.userID,
                caption: caption,
                file,
                cityName: user?.cityName,
                countryName: user?.countryName,
            }, (result: boolean) => {
                if (result) {
                    toast('Created Post!');
                    navigate('/tasks')
                }
                disableLoader()
            })
        }
    }

    function previewFile() {
        const fileRead = (ref?.current as any)?.files[0]
        const reader = new FileReader();

        reader.onloadend = function () {
            (imageRef.current as any).src = reader.result;
        }

        if (fileRead) {
            reader.readAsDataURL(fileRead);
        } else {
            (imageRef.current as any).src = "";
        }
    }
    return (
        <form onSubmit={(ev) => {
            ev.preventDefault()
            handleSubmit()
        }} className='pt-3'>
            <div className='flex justify-between'>
                <div className='cursor-pointer' onClick={() => {
                    navigate(-1)
                }}>
                    <ArrowLeft className='text-black ml-1' />
                </div>
                <button type='submit' className='flex bg-green-100 justify-center items-center h-10 w-20 text-center font-bold rounded-lg font text-green-500 outline outline-1'>
                    Post
                    <div className='h-6'>
                        <img src={checkImage} className='h-6 pl-1' />
                    </div>
                </button>
            </div>

            <div className='mt-4 '>
                <h1 className='text-2xl font-semibold'>Complete Task,</h1>
                <p className='font-semibold text-gray-500 pb-5'>Post an image to mark the task complete!</p>

                <div className='h-80 bg-green-200 grid justify-items-center rounded-lg'>
                    <h4 className='font-semibold text-xl p-2'>
                        Post Image
                    </h4>
                    <input ref={ref as any} onChange={(ev) => {
                        if (ev.target.files) {
                            setFile(ev.target.files?.[0])
                            previewFile()
                        }
                    }} type='file' accept='.png,.jpg' className='hidden' />
                    <img onClick={() => {
                        (ref?.current as any)?.click();
                    }} ref={imageRef as any} width={100} height={100} src={file ?? add} className='w-[200px] h-[200px] p-2 bg-white rounded-full object-contain' />
                    <p className='p-1 flex-col justify-center text-center text-sm '>
                        <span className='font-semibold'>Note:</span> Best when image is a square <br />
                        Ex: 1080x1080p
                    </p>
                </div>
                <br />
                <div className=' bg-white  grid justify-items-center rounded-lg '>
                    <div className='font-bold p-2 text-black'>
                        Post Caption
                    </div>
                    <EcoInput minLength={5} value={caption} required={true} extraStyle='w-10/12  mx-auto shadow-none border-0 border-b-2 border-b-green-400 rounded-sm h-[100]' handleChange={(val) => {
                        setCaption(val)
                    }} type="text" placeholder="Some Creative Caption" />
                </div>
                <BottomNavbar />
            </div>
        </form>
    )
}

export default AddPost