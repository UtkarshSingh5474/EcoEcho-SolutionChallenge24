import { Loader2Icon } from 'lucide-react'

const ScreenLoader = () => {
    return (
        <div className='h-screen fixed top-0 left-0 w-screen flex items-center justify-center'>
            <div>
                <Loader2Icon className='animate-spin' size={25} />
            </div>
        </div>
    )
}

export default ScreenLoader