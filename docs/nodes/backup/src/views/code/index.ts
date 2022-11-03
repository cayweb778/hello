import axios from 'axios';
import qs from 'qs';
export async function makeCode(){
    // console.log(111)
    // console.log(import.meta)
    // console.log(111)
    // fs.appendFile(__dirname + '/a.sql', 'asdsasad', err => console.log(err));

    axios({
        url:'/api/getUserInfoById',
        method:'POST',
        data:{
            id:'assada'
        }
    })
}