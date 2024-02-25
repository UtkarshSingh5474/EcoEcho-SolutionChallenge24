/* eslint-disable @typescript-eslint/no-explicit-any */
import leaderBoardIcon from '../assets/icons/ic_leaderboard.svg'
import feedIcon from '../assets/icons/ic_feed.svg'
import profileIcon from '../assets/icons/ic_default_profile.svg'
import tasksIcon from '../assets/icons/ic_task.svg'
import { BottomNavigation, BottomNavigationAction, Paper } from '@mui/material'
import { Link } from 'react-router-dom'
import { useUserStore } from '../store/user.store'
import { useState } from 'react'
const BottomNavbar = () => {
    const { user } = useUserStore();
    const [activeTab, setActivetab] = useState<any>('Tasks')
    const navbarConfig = [
        {
            title: 'Leaderboard',
            icon: leaderBoardIcon,
            route: '/leaderboard',
        },
        {
            title: 'Tasks',
            icon: tasksIcon,
            route: '/tasks',
        },
        {
            title: 'Feed',
            icon: feedIcon,
            route: '/feed',
        },
        {
            title: 'Profile',
            icon: profileIcon,
            route: '/profile/' + user?.userID,
        },
    ]
    return (
        <Paper className='w-full md:w-1/4' sx={{ position: 'fixed', bottom: 0, left: 0, right: 0, margin: 'auto' }}>
            <BottomNavigation onChange={(event, newVal) => {
                console.log(event)
                setActivetab(newVal)
            }}>
                {
                    navbarConfig.map(item => {
                        return <BottomNavigationAction
                            component={Link}
                            to={item.route}
                            label={item.title}
                            value={item.title}
                            icon={
                                activeTab === item.title ? <p>{item.title}</p> : <img src={item.icon} className='pointer-events-none' width={30} height={30} alt='bottom-bar icon' />
                            }
                        />
                    })
                }
            </BottomNavigation>
        </Paper>
    )
}

export default BottomNavbar