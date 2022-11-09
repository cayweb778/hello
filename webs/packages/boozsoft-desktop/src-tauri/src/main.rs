#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]
use std::io::Write;
use std::{fs, io,env, path::Path};
// Learn more about Tauri commands at https://tauri.app/v1/guides/features/command

use std::io::Read;

fn hasFilenameExist(path:&str, filename:&str) -> bool {
    let dir =std::fs::read_dir(path).unwrap();
    for x in dir {
        if let Ok(path) = x {
            // 是否存在某个文件
            if path.file_name().eq(filename) {
                return true;
            }
        }
    }
    return false
}
fn getTxt(path:&str, filename:&str) -> String {
    let filepath=format!("{}/{}",path,filename);
    println!("{}",filepath);
    let mut file = std::fs::File::open(filepath).unwrap();
    let mut contents = String::new();
    file.read_to_string(&mut contents).unwrap();
    return contents.to_string();
}
use std::collections::HashMap;
fn usepathInfo() -> HashMap<String, String> {
    let mut scores = HashMap::new();
    scores.insert(String::from("dirpath"),  String::from(std::env::temp_dir().display().to_string()));
    scores.insert(String::from("filename"), String::from("NcServerAddr.txt"));
    return scores;
}

use serde_json::Result;
use serde::{Deserialize, Serialize};
#[tauri::command]
fn getCacheIpAddr() -> String {
    println!("{}1","");
    let pathInfo=usepathInfo();
    let dirpath =pathInfo.get("dirpath").unwrap();
    let filename=pathInfo.get("filename").unwrap();
    let isExist=hasFilenameExist(&dirpath,&filename);
    println!("{}2",dirpath);
    println!("{}2",filename);

    let mut scores = HashMap::new();
    if isExist {
        println!("{}3","");

        scores.insert(String::from("code"),  String::from("200"));
        scores.insert(String::from("msg"), String::from("成功获取"));
        scores.insert(String::from("data"), String::from(getTxt(&dirpath, filename)));

    }else{
        scores.insert(String::from("code"), String::from("404"));
        scores.insert(String::from("msg"), String::from("找不到"));
        scores.insert(String::from("data"), String::from(""));
    }
    let a=serde_json::to_string(&scores).unwrap();
    return a;
}
#[tauri::command]
fn generate(name:String){
    let pathInfo=usepathInfo();
    let dirpath =pathInfo.get("dirpath").unwrap();
    let filename=pathInfo.get("filename").unwrap();
    println!("无敌：{}",dirpath);
    println!("无敌：{}",filename);
    let mut file = std::fs::File::create(format!("{}{}",dirpath,filename)).expect("create failed");
    file.write_all(name.as_bytes()).expect("write failed");

}

fn main() {
    tauri::Builder::default()
        .invoke_handler(tauri::generate_handler![
                                                      generate,
                                                      getCacheIpAddr
                                                    ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
