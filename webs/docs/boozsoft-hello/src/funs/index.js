import {pinyin} from "pinyin-pro";
import {replaceStr} from "./index2";


function getTwoIndex(mainIndex) {
    return mainIndex - mainIndex % 100000
}


// 三级目录
export function threeDir(twoDir, {parentId}) {
    let index = parentId
    twoDir.child = twoDir.child.map(it2 => {

        return {
            id: ++index,
            name: it2,
            parentId,
            enName: replaceStr(pinyin(it2, {toneType: 'none'})).replaceAll(' ', '')
        }
    })
}


// 二级目录
export function twoDir(child, {parentId}) {

    child.forEach(it1 => {
        it1.parentId = parentId
        it1.id = parentId + 1000
        it1.enName = replaceStr(pinyin(it1.name, {toneType: 'none'})).replaceAll(' ', '')

        threeDir(it1, {parentId: it1.id})
    })
}

// 一级目录
export function oneDir(arr, mainIndex) {
    arr.forEach(it => {
        mainIndex += 100000
        it.id = getTwoIndex(mainIndex)

        twoDir(it.child, {parentId: it.id})
        // mainIndex = mainIndex - getTwoIndex()
    })
}