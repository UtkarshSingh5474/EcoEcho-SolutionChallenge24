/* eslint-disable @typescript-eslint/no-explicit-any */
import { X } from "lucide-react"
import { TaskProps } from "../types/task.types"
import { Divider, Modal, Typography } from "@mui/material"
import Box from '@mui/material/Box';
import { getCategoryImagePath } from "../utils/mapper";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    height: 450,
    border: '5px solid #fff',
    bgcolor: 'background.paper',
    p: 4,
    borderRadius: '10px'
};
const TaskInfoPopup = ({ data, hideFn }: { data: TaskProps, hideFn: () => void }) => {

    return <Modal
        open={true}
        onClose={hideFn}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
        disableAutoFocus
    >
        <Box component={null as any} sx={style} border={'none'} cu>
            <div onClick={hideFn} className="cursor-pointer absolute -top-3 -right-3 bg-red-500 w-fit p-2 rounded-full">
                <X size={22} color="white" fontWeight={800} />
            </div>
            <Typography id="modal-modal-title" variant="h6" className="text-center" component="h2">
                {data.category}
            </Typography>
            <img src={getCategoryImagePath(data.category)} className="mx-auto my-3" width={60} height={60} />
            <Divider className="bg-green-600 h-[2px] mt-3" />
            <Typography id="modal-modal-description" sx={{ mt: 2 }}>
                {data.title}
            </Typography>
            <div className="bg-white mx-auto border-[.5px] text-[9px] text-gray-600 my-2 p-2 rounded-lg">
                <p className="text-sm">{data.desc}</p>
            </div>
        </Box>
    </Modal>
}

export default TaskInfoPopup