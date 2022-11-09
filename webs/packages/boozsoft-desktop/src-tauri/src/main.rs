#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]
use std::io::Write;
use std::{fs, io,env, path::Path};
// Learn more about Tauri commands at https://tauri.app/v1/guides/features/command

use std::io::Read;
#[tauri::command]
fn greet(name: &str) -> String {
     let mut file = std::fs::File::create("data.txt").expect("create failed");
       file.write_all(name.as_bytes()).expect("write failed");
       println!("data written to file" );
    format!("Hello, {}! You've been greeted from Rust!", name)
}
#[tauri::command]
fn asdsadas(name: &str) -> String {
 let path = env::current_dir().unwrap();
   let dir = path.as_path().read_dir().unwrap();
    for x in dir {
       if let Ok(path) = x {
           // 是否存在某个文件
           if path.file_name().eq("data.txt") {
               let mut file = std::fs::File::open("data.txt").unwrap();
               let mut contents = String::new();
               file.read_to_string(&mut contents).unwrap();
               print!("{}", contents);
              return  format!("{}",contents );
           }
        }
    }
    return  "".to_string()

}

fn main() {
    tauri::Builder::default()
        .invoke_handler(tauri::generate_handler![
                                                      asdsadas,
                                                      greet
                                                    ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
