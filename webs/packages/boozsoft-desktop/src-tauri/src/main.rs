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
   let mut file = std::fs::File::open("data.txt").unwrap();
     let mut contents = String::new();
     file.read_to_string(&mut contents).unwrap();
     print!("{}", contents);
     format!("{}",contents )
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
