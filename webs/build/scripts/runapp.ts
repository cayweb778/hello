// "start": "run-p start:*"
const prompts = require('prompts');

const process = require("child_process");

~async function () {

    const createChoice = (title, disable) => ({title: "⭐️ " + title, value: title, disable: !!disable});
    const arr = [
        ["存货", "start:stock"],
        ["总账", "start:zongzhang"],
        ["固定资产", "start:gdzc"],
        ["系统", "start:system"],
        ["主页", "start:home"],
    ]
    const arr2 = [
        ["主数据弹出框", "start:modals"],
        ["数据导出控件", "start:print"],
        ["打印预览控件", "start:pdfviewer"],
        ["excel预览控件", "start:excelviewer"],
        ["表单设计器", "start:boozsoft-table-design"],
    ]
    const res = await prompts([
        {
            type: 'multiselect', // 多选
            name: 'weapons',
            message: '🚀🚀🚀 模块选择 🚀🚀🚀',
            instructions: '',
            hint: '-  ️ 空格选择. 回车确认️ ',
            choices: [...arr.map(it => createChoice(it[0], null)), createChoice('所有', null),]
        },
        {
            type: 'multiselect', // 多选
            name: 'plugins',
            message: '🚀🚀🚀 插件选择 🚀🚀🚀',
            instructions: '',
            hint: '-  ️ 默认不开启，️ ',
            choices: [...arr2.map(it => createChoice(it[0], null))]
        },

    ]);

    function findA(str) {
        return arr.filter(it => it[0] == str)[0][1]
    }

    function findB(str) {
        return arr2.filter(it => it[0] == str)[0][1]
    }

    if (res.weapons == null) {
        console.log("！👏！👏！👏 已结束运行 ！👏！👏！👏")
        return
    }
    process.spawn('pnpm start:desktop', {
        stdio: 'inherit',
        shell: true
    });
    process.spawn('pnpm start:main', {
        stdio: 'inherit',
        shell: true
    });


    if (res.weapons.indexOf("所有") != -1) {
        arr.forEach(it => {
            process.spawn('pnpm ' + it[1], {
                stdio: 'inherit',
                shell: true
            });
        })
        return
    }

    res.weapons.forEach(it => {
        process.spawn('pnpm ' + findA(it), {
            stdio: 'inherit',
            shell: true
        });
    })
    res.plugins.forEach(it => {
        process.spawn('pnpm ' + findB(it), {
            stdio: 'inherit',
            shell: true
        });
    })
    // process.exec("open /Applications/csd.app",function(err,stdout,stderr){
    //     if(err){
    //         console.log("子进程启动失败：",err);
    //         process.exit();
    //     }else{
    //         console.log("子进程标准输出：",stdout.toString());
    //     }
    // })
}();