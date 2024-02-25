/* eslint-disable @typescript-eslint/no-explicit-any */
import leaderBoardIcon from '../assets/icons/ic_leaderboard.svg'
import feedIcon from '../assets/icons/ic_feed.svg'
import profileIcon from '../assets/icons/ic_default_profile.svg'
import tasksIcon from '../assets/icons/ic_task.svg'
import { BottomNavigation, BottomNavigationAction, Paper } from '@mui/material'
import { Link, useLocation } from 'react-router-dom'
import { useUserStore } from '../store/user.store'
const BottomNavbar = () => {
    const { user } = useUserStore();
    const location = useLocation();
    const navbarConfig = [
        {
            title: 'leaderboard',
            icon: leaderBoardIcon,
            route: '/leaderboard',
        },
        {
            title: 'tasks',
            icon: tasksIcon,
            route: '/tasks',
        },
        {
            title: 'feed',
            icon: feedIcon,
            route: '/feed',
        },
        {
            title: 'profile',
            icon: profileIcon,
            route: '/profile/' + user?.userID,
        },
    ]
    return (
        <Paper className='w-full md:w-1/4' sx={{ position: 'fixed', bottom: 0, left: 0, right: 0, margin: 'auto' }}>
            <BottomNavigation>
                {
                    navbarConfig.map(item => {
                        return <BottomNavigationAction
                            component={Link}
                            to={item.route}
                            label={item.title}
                            value={item.title}
                            icon={
                                <img src={item.icon} className={
                                    `pointer-events-none ${location.pathname.includes(item.title) ? 'bg-green-100 p-2 transition-all rounded-full' : 'p-2 rounded-full'}`
                                } width={40} height={40} alt='bottom-bar icon' />
                            }
                        />
                    })
                }
            </BottomNavigation>
        </Paper>
    )
}

export default BottomNavbar