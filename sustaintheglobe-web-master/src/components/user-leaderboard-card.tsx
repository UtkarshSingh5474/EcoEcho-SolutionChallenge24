import { UserProps } from '../types/user.types'
import ic_default_profile from '../assets/icons/ic_default_profile.svg'
import ic_points from '../assets/icons/ic_points.svg';
import { useNavigate } from 'react-router-dom';

const UserLeaderBoardCard = ({ data, rank }: { data: UserProps, rank: number }) => {
    const navigate = useNavigate();
    return (
        <div onClick={() => navigate(`/profile/${data.userID}`)} className='cursor-pointer flex items-center justify-start my-3'>
            <h3 className='font-semibold text-sm'>#{rank}</h3>
            <div className='flex items-center justify-start ml-2'>
                <img src={ic_default_profile} width={30} height={30} alt='user-image' />
                <div className='ml-2'>
                    <h4 className='font-semibold mb-0 '>{data.fullName}</h4>
                    <h4 className='text-xs text-gray-400 -mt-1'>@{data.username}</h4>
                </div>

            </div>
            <div className='flex items-center justify-start ml-auto'>
                <img src={ic_points} width={20} height={20} alt='user-image' />
                <h4 className='font-semibold text-sm p-0 -mt-[.7px] ml-1'>{data.points}</h4>
            </div>

        </div>
    )
}

export default UserLeaderBoardCard