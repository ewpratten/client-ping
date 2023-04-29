use jni::{
    objects::{JClass, JString},
    JNIEnv,
};

/// A simple "Hello World" function, used to check if this lib loaded from the logfiles
#[no_mangle]
pub extern "system" fn Java_com_ewpratten_client_1ping_Native_helloWorld<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
) -> JString<'local> {
    return env
        .new_string("Hello from Rust!")
        .expect("Couldn't create java string!");
}


