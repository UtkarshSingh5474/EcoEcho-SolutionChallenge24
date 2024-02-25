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
    const { tasks, saveTasks } = useTaskStore()

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
                <h1 className='text-2xl font-semibold'>My Tasks</h1>
                <p className='font-semibold text-lg text-gray-400'>Your currently assigned tasks</p>

                <p className='text-sm font-semibold mt-5'>Daily Tasks</p>
                {
                    getDailyTasks(tasks ?? []).map(item => {
                        return <TaskCard clickHandler={() => {
                            setSelectedCard(item)
                        }} data={item} type='daily' />
                    })
                }

                <p className='text-sm font-semibold mt-5'>Weekly Tasks</p>
                {
                    getWeeklyTasks(tasks ?? []).map(item => {
                        return <TaskCard clickHandler={() => {
                            setSelectedCard(item)
                        }} data={item} type='weekly' />
                    })
                }

                <p className='text-sm font-semibold mt-5'>Monthly Tasks</p>
                {
                    getMonthlyTasks(tasks ?? []).map(item => {
                        return <TaskCard clickHandler={() => {
                            setSelectedCard(item)
                        }} data={item} type='monthly' />
                    })
                }
                <BottomNavbar />
            </div>
        </div>
    )
}

export default TaskPage