use printers::{get_printers, print};
use serde_json::Result;

#[tauri::command]
pub fn get_printers_all() -> String {
    let printers = get_printers();
    println!("{:?}", printers);
    printers::print(&printers[1], "42".as_bytes());
    // return serde_json::to_string(&printers);
    return "".to_string();
}

#[tauri::command]
pub fn printData() -> String {
    let printers = get_printers();
    println!("{:?}", printers);
    printers::print(&printers[1], "42".as_bytes());
    return "".to_string();
}