use printers::{get_printers};

#[tauri::command]
pub fn get_printers_all() -> String {
    let printers = get_printers();
    let aaaa=format!("{:?}", printers);
    // printers::print(&printers[1], "42".as_bytes());
    // let ddddd=serde_json::to_string(&printers);
    return aaaa.to_string();
}
use base64::{ decode as base64Decode};
#[tauri::command]
pub fn print_data(contents:&str) -> String {

    let printers = get_printers();

    let base64_decode_vec=base64Decode(contents).unwrap();

    printers::print(&printers[1],&base64_decode_vec);
    return "".to_string();
}