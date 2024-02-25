/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from 'react';
import BottomNavbar from '../../components/bottom-nav'
import ScreenLoader from '../../components/screen-loader';
import TaskCard from '../../components/task-card';
import { TaskProps } from '../../types/task.types';
import { getDailyTasks, getMonthlyTasks, getWeeklyTasks } from '../../utils/handler';
import TaskInfoPopup from '../../components/task-info-popup';
import { useUserStore } from '../../store/user.store';
import { useLoaderStore } from '../../store/loader.store';
import { useTaskStore } from '../../store/task.store';
import { getOrCreateUserTasks } from '../../helpers/task-helper';


const TaskPage = () => {
    const { user } = useUserStore();
    const { enableLoader, disableLoader } = useLoaderStore();
    const [selectedCard, setSelectedCard] = useState<TaskProps | null>(null)
    const { tasks, saveTasks, removeTasks } = useTaskStore()

    useEffect(() => {
        async function assignUserTasks() {
            if (user?.userID) {
                enableLoader();
                const userTasks = await getOrCreateUserTasks(user.userID, user.currentTasks);
                console.log(userTasks)
                saveTasks(userTasks)
                disableLoader()
            }
        }

        assignUserTasks()

        return () => {
            removeTasks()
        }
    }, [])


    if (!tasks) {
        return <ScreenLoader />
    }
    return (
        <div>
            {selectedCard ? <TaskInfoPopup hideFn={() => {
                setSelectedCard(null)
            }} data={selectedCard} /> : null}
            <div className='mt-5'>
                <h1 className='text-xl font-semibold'>My Tasks</h1>
                <p className='font-semibold text-md text-gray-400'>Your currently assigned tasks</p>

                <div className='pb-[100px] mt-5'>
                    <div className='flex items-center justify-center'>
                        <div className='h-[2px] bg-green-300 w-full'></div>
                        <p className='text-sm font-semibold text-center w-full'>Daily Tasks</p>
                        <div className='h-[2px] bg-green-300 w-full'></div>
                    </div>
                    {
                        getDailyTasks(tasks ?? []).map(item => {
                            return <TaskCard clickHandler={() => {
                                setSelectedCard(item)
                            }} data={item} type='daily' />
                        })
                    }

                    <div className='flex items-center justify-center'>
                        <div className='h-[2px] bg-orange-300 w-full'></div>
                        <p className='text-sm font-semibold text-center w-full'>Weekly Tasks</p>
                        <div className='h-[2px] bg-orange-300 w-full'></div>
                    </div>
                    {
                        getWeeklyTasks(tasks ?? []).map(item => {
                            return <TaskCard clickHandler={() => {
                                setSelectedCard(item)
                            }} data={item} type='weekly' />
                        })
                    }

                    <div className='flex items-center justify-center'>
                        <div className='h-[2px] bg-red-300 w-full'></div>
                        <p className='text-sm font-semibold text-center w-full'>Monthly Tasks</p>
                        <div className='h-[2px] bg-red-300 w-full'></div>
                    </div>
                    {
                        getMonthlyTasks(tasks ?? []).map(item => {
                            return <TaskCard clickHandler={() => {
                                setSelectedCard(item)
                            }} data={item} type='monthly' />
                        })
                    }
                </div>
                <BottomNavbar />
            </div>
        </div>
    )
}

export default TaskPage