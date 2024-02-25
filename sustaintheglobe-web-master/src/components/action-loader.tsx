import { Loader2Icon } from 'lucide-react'

const ActionLoader = () => {
    return (
        <div className='h-screen bg-black bg-opacity-50 z-10 w-screen fixed top-0 left-0 flex items-center justify-center'>
            <div className='rounded-full border-4 border-green-300 border-offset-2 bg-white p-4'>
                <Loader2Icon className='animate-spin' size={25} />
            </div>
        </div>
    )
}

export default ActionLoader