// "start": "run-p start:*"
const prompts = require('prompts');

const process = require("child_process");
~async function () {
    const createChoice = (title, disable) => ({ title:"⭐️ "+title, value: title, disable: !!disable });
    const arr=[

        ["桌面应用","start:desktop"],
        ["登陆","start:auth"],
        ["弹出框组件","start:modals"],
        ["打印组件","start:print"],
        ["主页","start:home"],
        ["系统","start:system"],
        ["总账","start:zongzhang"],
        ["存货","start:stock"],
        ["固定资产","start:gdzc"],
    ]
    const res = await prompts([

        {
            type: 'multiselect', // 多选
            name: 'weapons',
            message: '🚀🚀🚀 选择开启的模块 🚀🚀🚀',
            instructions:'',
            hint: '-  ️ 空格选择. 回车确认️ ',
            choices: [createChoice('所有',null),...arr.map(it=> createChoice(it[0],null))]
        }
    ]);

    function findA(str){
        return arr.filter(it=>it[0]==str)[0][1]
    }
    if(res.weapons==null){
        console.log("！👏！👏！👏 已结束运行 ！👏！👏！👏")
        return
    }
    process.spawn('pnpm start:main', {
        stdio:'inherit',
        shell:true
    });


    if(res.weapons.indexOf("所有")!=-1){
        arr.forEach(it=>{
            process.spawn('pnpm '+it[1], {
                stdio:'inherit',
                shell:true
            });
        })
        return
    }

    res.weapons.forEach(it=>{
        process.spawn('pnpm '+findA(it), {
            stdio:'inherit',
            shell:true
        });
    })
}();