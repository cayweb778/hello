#![cfg_attr(
all(not(debug_assertions), target_os = "windows"),
windows_subsystem = "windows"
)]

use std::io::Write;
use std::{fs, io, env, path::Path};
use std::borrow::Borrow;
// Learn more about Tauri commands at https://tauri.app/v1/guides/features/command

use std::io::Read;

fn hasFilenameExist(path: &str, filename: &str) -> bool {
    let dir = std::fs::read_dir(path).unwrap();
    for x in dir {
        if let Ok(path) = x {
            // 是否存在某个文件
            if path.file_name().eq(filename) {
                return true;
            }
        }
    }
    return false;
}

fn getTxt(path: &str, filename: &str) -> String {
    let filepath = format!("{}{}", path, filename);
    println!("{}", filepath);
    let mut file = std::fs::File::open(filepath).unwrap();
    let mut contents = String::new();
    file.read_to_string(&mut contents).unwrap();
    return contents.to_string();
}

use std::collections::HashMap;
use std::fmt::format;
use std::fs::File;

fn usepathInfo() -> HashMap<String, String> {
    let mut scores = HashMap::new();
    scores.insert(String::from("dirpath"), String::from(std::env::temp_dir().display().to_string()));
    scores.insert(String::from("filename"), String::from("NcServerAddr.txt"));
    return scores;
}

use serde_json::Result;
use serde::{Deserialize, Serialize};

fn getCacheIpAddr() -> HashMap<String, String> {
    let pathInfo = usepathInfo();
    let dirpath = pathInfo.get("dirpath").unwrap();
    let filename = pathInfo.get("filename").unwrap();
    let isExist = hasFilenameExist(&dirpath, &filename);

    let mut scores = HashMap::new();
    if isExist {
        scores.insert(String::from("code"), String::from("200"));
        scores.insert(String::from("msg"), String::from("成功获取"));
        scores.insert(String::from("data"), String::from(getTxt(&dirpath, filename)));
    } else {
        scores.insert(String::from("code"), String::from("404"));
        scores.insert(String::from("msg"), String::from("找不到"));
        scores.insert(String::from("data"), String::from(""));
    }
    // let a=serde_json::to_string(&scores).unwrap();
    return scores;
}

#[tauri::command]
fn getCacheIpAddrApi() -> String {
    let scores = getCacheIpAddr();
    return serde_json::to_string(&scores).unwrap();
}


#[tauri::command]
fn hello() -> bool {
    let pathInfo = usepathInfo();
    let dirpath = pathInfo.get("dirpath").unwrap();
    let filename = pathInfo.get("filename").unwrap();
    let isExist = hasFilenameExist(&dirpath, &filename);

    if isExist {
        return true;
    } else {
        return false;
    }
}

#[tauri::command]
fn generate(name: String) -> String {
    let pathInfo = usepathInfo();
    let dirpath = pathInfo.get("dirpath").unwrap();
    let filename = pathInfo.get("filename").unwrap();
    println!("无敌：{}", dirpath);
    println!("无敌：{}", filename);
    let mut file = std::fs::File::create(format!("{}{}", dirpath, filename)).expect("create failed");
    file.write_all(name.as_bytes()).expect("write failed");
    return format!("{}::{}::{}", dirpath, filename, dirpath);
}

#[tauri::command]
fn goApp(name: String, window: Window) -> String {
    goApp2(window.app_handle().borrow());
    return "".to_string();
    // let pathInfo=usepathInfo();
    // let dirpath =pathInfo.get("dirpath").unwrap();
    // let filename=pathInfo.get("filename").unwrap();
    // println!("无敌：{}",dirpath);
    // println!("无敌：{}",filename);
    // let mut file = std::fs::File::create(format!("{}{}",dirpath,filename)).expect("create failed");
    // file.write_all(name.as_bytes()).expect("write failed");
    // return format!("{}::{}::{}",dirpath,filename,dirpath)
}

fn printData() {
    let printers = get_printers();
    println!("{:?}", printers);
    printers::print(&printers[1], "42".as_bytes());
}

use tauri::{App, AppHandle, Builder, Manager, Wry};
use printers::{get_printers, print};

fn createNew() {
    // let pathInfo=usepathInfo();
    // let dirpath =pathInfo.get("dirpath").unwrap();
    // let filename=pathInfo.get("filename").unwrap();
    //
    //
    // let file = fs::File::open("tauri.conf.json")
    //     .expect("file should open read only");
    // let mut json: serde_json::Value = serde_json::from_reader(file)
    //     .expect("file should be proper JSON");
    // let mut t = serde_json::json!({});
    // json["build"]["devPath"]= serde_json::Value::String("http://81.70.47.206:81".to_string());
    // println!("{:?}",json);
    // println!("{:?}",dirpath);
    // let mut hello=File::create(format!("{}{}",dirpath,"abc.json")).unwrap();
    // hello.write_all(json.to_string().as_bytes());
}
// register shortcut

fn goApp2(app: &AppHandle<Wry>) {
    println!("{}", "sdassaassada");
    let cacheIpAddrMap = getCacheIpAddr();
    let code = cacheIpAddrMap["code"].clone();
    let url = cacheIpAddrMap["data"].clone();


    let windowUrl = format!("http://{}/nc", url);
    let name;
    if (app.get_window("main2333").is_none()) {
        if (!app.get_window("main23333333").is_none()) {
            app.get_window("main23333333").unwrap().close();
        }
        name = "main2333";
    } else {
        name = "main23333333";
        app.get_window("main2333").unwrap().close();
    }
    let win = tauri::window::WindowBuilder::new(app, name.to_string(), WindowUrl::App(windowUrl.into()))
        .title("财税达ERP-NC企业管理软件")
        .maximized(true)
        .inner_size(600.0, 400.0)
        .build().unwrap();
}

use tauri::{Menu, Submenu, MenuItem, command, window::WindowBuilder, Window, WindowUrl, RunEvent, GlobalShortcutManager};
use tauri::{
    api::dialog::ask, CustomMenuItem, WindowEvent,
};

mod shortCar;

fn main() {
    let submenu_gear = Submenu::new(
        "Gear",
        Menu::new()
            .add_native_item(MenuItem::Copy)
            .add_native_item(MenuItem::Paste)
            .add_native_item(MenuItem::Separator)
            .add_native_item(MenuItem::Zoom)
            .add_native_item(MenuItem::Separator)
            .add_native_item(MenuItem::Hide)
            .add_native_item(MenuItem::CloseWindow)
            .add_native_item(MenuItem::Quit),
    );
    let close = CustomMenuItem::new("close".to_string(), "关闭");
    let quit = CustomMenuItem::new("quit".to_string(), "退出");
    let set = CustomMenuItem::new("set".to_string(), "设置");
    let submenu_customer = Submenu::new(
        "财税达菜单",
        Menu::new()
            .add_item(close)
            .add_item(quit)
            .add_item(set),
    );
    let menus = Menu::new()
        .add_submenu(submenu_gear)
        .add_submenu(submenu_customer);
    fn abcsss() {}
    let app = tauri::Builder::default()
        // 添加菜单
        .menu(menus)

        // 监听自定义菜单事件
        .on_menu_event(|event| match event.menu_item_id() {
            "quit" => {
                std::process::exit(0);
            }
            "close" => {
                event.window().close().unwrap();
            }
            _ => {}
        })
        .setup(|app| {
            shortCar::registerShort(app);


            let cacheIpAddrMap = getCacheIpAddr();
            let code = cacheIpAddrMap["code"].clone();
            let url = cacheIpAddrMap["data"].clone();
            let windowUrl = format!("http://{}/nc", url);

            if (code == "200") {
                goApp2(app.handle().borrow());
            } else {
                let win = tauri::window::WindowBuilder::new(app, "main".to_string(), WindowUrl::default())
                    .title("财税达ERP-NC企业管理软件")
                    .maximized(true)
                    .inner_size(600.0, 400.0)
                    .build()?;
                // win.open_devtools();
            }


            Ok(())
        })

        .invoke_handler(tauri::generate_handler![generate,goApp,getCacheIpAddrApi]);
    app.run(tauri::generate_context!())
        .expect("error while running tauri application");
    // let mut bvvv=json.get_mut("build")
    //     .expect("file should have FirstName key");
    //     .expect("file should have FirstName key")
    //     ;
    // bvvv=t;
}
