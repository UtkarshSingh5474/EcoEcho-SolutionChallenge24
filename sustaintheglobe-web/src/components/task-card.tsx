/* eslint-disable @typescript-eslint/no-explicit-any */
import { useNavigate } from 'react-router-dom'
import checkImage from '../assets/icons/ic_tick.svg'
import { TaskProps } from '../types/task.types'
import { getCategoryImagePath } from '../utils/mapper'
import { toast } from 'react-toastify'

const TaskCard = ({ data, type, clickHandler }: { data: TaskProps, type: string, clickHandler: () => void }) => {
    const navigate = useNavigate();
    const bgColor: any = {
        'daily': 'bg-green-100',
        'weekly': 'bg-orange-100',
        'monthly': 'bg-red-100'
    }

    const scoreMapper = {
        "1": "3",
        "2": '10',
        "3": '30'
    }
    return (
        <div onClick={(ev: any) => {
            if (!['task-cta', 'task-cta-div'].includes(ev.target.id)) {
                clickHandler()
            } else {
                if (data.postID) {
                    toast('Already Posted!');
                    return;
                }
                navigate(`/${data.taskID}/addpost`)
            }
        }} className={`cursor-pointer border-2 flex items-center justify-around py-3 px-3 my-3 rounded-lg ${bgColor[type]}`}>
            <div className='p-1 bg-white border-2 rounded-xl'>
                <img src={getCategoryImagePath(data.category)} width={40} height={40} />
            </div>
            <div className='w-8/12 ml-3 '>
                <h4 className='font-semibold'>{data.title}</h4>
                <p className='font-semibold text-green-600 text-sm'>+{scoreMapper[data.level]} Points</p>
            </div>
            <div id='task-cta' className='p-1 bg-white hover:bg-green-100 transition-all cursor-pointer border-2 rounded-xl'>
                {data.postID ? <img src={checkImage} alt="check-image" width={50} height={50} /> : <div id='task-cta-div' className='w-[50px] h-[50px] border-green-400 border-2 rounded-md'></div>}
            </div>
        </div>
    )
}

export default TaskCard