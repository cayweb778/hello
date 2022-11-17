use std::borrow::Borrow;
use tauri::{Menu, Submenu, MenuItem, command, window::WindowBuilder, Window, WindowUrl, RunEvent, GlobalShortcutManager, Wry, Manager};
use tauri::{
    App,
    api::dialog::ask, CustomMenuItem,  WindowEvent,
};

pub fn registerShort(app:&App<Wry>){

    let appHandleClone=app.handle().clone();
    app.global_shortcut_manager().register("Shift+Escape", move|| {
        let window1 = appHandleClone.borrow().get_window("config");
        if(window1.is_none()){
            let win = tauri::window::WindowBuilder::new(appHandleClone.borrow(), "config".to_string(), WindowUrl::App("config".into()))
                .title("财税达配置选项")
                .inner_size(600.0, 360.0)
                .always_on_top(true)
                .hidden_title(true)
                .decorations(false)

                .build().unwrap();
        }else{
            if(window1.clone().unwrap().is_visible().unwrap()){

                window1.clone().unwrap().hide().expect("TODO: panic message");
            }else{
                window1.clone().unwrap().show().expect("TODO: panic message");
            }
        }


    }).expect("TODO: panic message");

}
