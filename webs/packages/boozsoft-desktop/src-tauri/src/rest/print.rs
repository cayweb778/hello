use printers::{get_printers, print};
use serde_json::Result;

#[tauri::command]
pub fn get_printers_all() -> String {
    let printers = get_printers();
    let aaaa=format!("{:?}", printers);
    // printers::print(&printers[1], "42".as_bytes());
    // let ddddd=serde_json::to_string(&printers);
    return aaaa.to_string();
}
use base64::{encode as base64encode, decode as base64Decode};
#[tauri::command]
pub fn printData(contents:&str) -> String {

    let printers = get_printers();

    let base64DecodeVec=base64Decode(contents).unwrap();

    printers::print(&printers[1],&base64DecodeVec);
    return "".to_string();
}