

;
(function() {


        ;
        (function() {

            /*! hotkeys-js v3.8.7 | MIT (c) 2021 kenny wong <wowohoo@qq.com> | http://jaywcjlove.github.io/hotkeys */
            !function(e, t) {
                "object" == typeof exports && "undefined" != typeof module ? module.exports = t() : "function" == typeof define && define.amd ? define(t) : (e = "undefined" != typeof globalThis ? globalThis : e || self).hotkeys = t()
            }(this, function() {
                "use strict";
                var e = "undefined" != typeof navigator && 0 < navigator.userAgent.toLowerCase().indexOf("firefox");
                function u(e, t, n) {
                    e.addEventListener ? e.addEventListener(t, n, !1) : e.attachEvent && e.attachEvent("on".concat(t), function() {
                        n(window.event)
                    })
                }
                function p(e, t) {
                    for (var n = t.slice(0, t.length - 1), o = 0; o < n.length; o++)
                        n[o] = e[n[o].toLowerCase()];
                    return n
                }
                function d(e) {
                    for (var t = (e = (e = "string" != typeof e ? "" : e).replace(/\s/g, "")).split(","), n = t.lastIndexOf(""); 0 <= n;)
                        t[n - 1] += ",",
                            t.splice(n, 1),
                            n = t.lastIndexOf("");
                    return t
                }
                for (var t = {
                    backspace: 8,
                    tab: 9,
                    clear: 12,
                    enter: 13,
                    return: 13,
                    esc: 27,
                    escape: 27,
                    space: 32,
                    left: 37,
                    up: 38,
                    right: 39,
                    down: 40,
                    del: 46,
                    delete: 46,
                    ins: 45,
                    insert: 45,
                    home: 36,
                    end: 35,
                    pageup: 33,
                    pagedown: 34,
                    capslock: 20,
                    num_0: 96,
                    num_1: 97,
                    num_2: 98,
                    num_3: 99,
                    num_4: 100,
                    num_5: 101,
                    num_6: 102,
                    num_7: 103,
                    num_8: 104,
                    num_9: 105,
                    num_multiply: 106,
                    num_add: 107,
                    num_enter: 108,
                    num_subtract: 109,
                    num_decimal: 110,
                    num_divide: 111,
                    "\u21ea": 20,
                    ",": 188,
                    ".": 190,
                    "/": 191,
                    "`": 192,
                    "-": e ? 173 : 189,
                    "=": e ? 61 : 187,
                    ";": e ? 59 : 186,
                    "'": 222,
                    "[": 219,
                    "]": 221,
                    "\\": 220
                }, y = {
                    "\u21e7": 16,
                    shift: 16,
                    "\u2325": 18,
                    alt: 18,
                    option: 18,
                    "\u2303": 17,
                    ctrl: 17,
                    control: 17,
                    "\u2318": 91,
                    cmd: 91,
                    command: 91
                }, h = {
                    16: "shiftKey",
                    18: "altKey",
                    17: "ctrlKey",
                    91: "metaKey",
                    shiftKey: 16,
                    ctrlKey: 17,
                    altKey: 18,
                    metaKey: 91
                }, m = {
                    16: !1,
                    18: !1,
                    17: !1,
                    91: !1
                }, g = {}, n = 1; n < 20; n++)
                    t["f".concat(n)] = 111 + n;
                var v = [],
                    o = "all",
                    w = [],
                    k = function(e) {
                        return t[e.toLowerCase()] || y[e.toLowerCase()] || e.toUpperCase().charCodeAt(0)
                    };
                function r(e) {
                    o = e || "all"
                }
                function O() {
                    return o || "all"
                }
                function f(e) {
                    var i = e.scope,
                        r = e.method,
                        t = e.splitKey,
                        f = void 0 === t ? "+" : t;
                    d(e.key).forEach(function(e) {
                        var t,
                            n = e.split(f),
                            o = n.length,
                            e = n[o - 1],
                            e = "*" === e ? "*" : k(e);
                        g[e] && (i = i || O(), t = 1 < o ? p(y, n) : [], g[e] = g[e].map(function(e) {
                            return r && e.method !== r || e.scope !== i || !function(e, t) {
                                for (var n = e.length < t.length ? t : e, o = e.length < t.length ? e : t, i = !0, r = 0; r < n.length; r++)
                                    ~o.indexOf(n[r]) || (i = !1);
                                return i
                            }(e.mods, t) ? e : {}
                        }))
                    })
                }
                function K(e, t, n) {
                    var o;
                    if (t.scope === n || "all" === t.scope) {
                        for (var i in o = 0 < t.mods.length, m)
                            Object.prototype.hasOwnProperty.call(m, i) && (!m[i] && ~t.mods.indexOf(+i) || m[i] && !~t.mods.indexOf(+i)) && (o = !1);
                        (0 !== t.mods.length || m[16] || m[18] || m[17] || m[91]) && !o && "*" !== t.shortcut || !1 === t.method(e, t) && (e.preventDefault ? e.preventDefault() : e.returnValue = !1, e.stopPropagation && e.stopPropagation(), e.cancelBubble && (e.cancelBubble = !0))
                    }
                }
                function b(n) {
                    var e = g["*"],
                        t = n.keyCode || n.which || n.charCode;
                    if (x.filter.call(this, n)) {
                        if (~v.indexOf(t = 93 === t || 224 === t ? 91 : t) || 229 === t || v.push(t), ["ctrlKey", "altKey", "shiftKey", "metaKey"].forEach(function(e) {
                            var t = h[e];
                            n[e] && !~v.indexOf(t) ? v.push(t) : !n[e] && ~v.indexOf(t) ? v.splice(v.indexOf(t), 1) : "metaKey" === e && n[e] && 3 === v.length && (n.ctrlKey || n.shiftKey || n.altKey || (v = v.slice(v.indexOf(t))))
                        }), t in m) {
                            for (var o in m[t] = !0, y)
                                y[o] === t && (x[o] = !0);
                            if (!e)
                                return
                        }
                        for (var i in m)
                            Object.prototype.hasOwnProperty.call(m, i) && (m[i] = n[h[i]]);
                        n.getModifierState && (!n.altKey || n.ctrlKey) && n.getModifierState("AltGraph") && (~v.indexOf(17) || v.push(17), ~v.indexOf(18) || v.push(18), m[17] = !0, m[18] = !0);
                        var r = O();
                        if (e)
                            for (var f = 0; f < e.length; f++)
                                e[f].scope === r && ("keydown" === n.type && e[f].keydown || "keyup" === n.type && e[f].keyup) && K(n, e[f], r);
                        if (t in g)
                            for (var a = 0; a < g[t].length; a++)
                                if (("keydown" === n.type && g[t][a].keydown || "keyup" === n.type && g[t][a].keyup) && g[t][a].key) {
                                    for (var c = g[t][a], l = c.key.split(c.splitKey), s = [], u = 0; u < l.length; u++)
                                        s.push(k(l[u]));
                                    s.sort().join("") === v.sort().join("") && K(n, c, r)
                                }
                    }
                }
                function x(e, t, n) {
                    v = [];
                    var o = d(e),
                        i = [],
                        r = "all",
                        f = document,
                        a = 0,
                        c = !1,
                        l = !0,
                        s = "+";
                    for (void 0 === n && "function" == typeof t && (n = t), "[object Object]" === Object.prototype.toString.call(t) && (t.scope && (r = t.scope), t.element && (f = t.element), t.keyup && (c = t.keyup), void 0 !== t.keydown && (l = t.keydown), "string" == typeof t.splitKey && (s = t.splitKey)), "string" == typeof t && (r = t); a < o.length; a++)
                        i = [],
                        1 < (e = o[a].split(s)).length && (i = p(y, e)),
                        (e = "*" === (e = e[e.length - 1]) ? "*" : k(e)) in g || (g[e] = []),
                            g[e].push({
                                keyup: c,
                                keydown: l,
                                scope: r,
                                mods: i,
                                shortcut: o[a],
                                method: n,
                                key: o[a],
                                splitKey: s
                            });
                    void 0 !== f && (t = f, !~w.indexOf(t)) && window && (w.push(f), u(f, "keydown", function(e) {
                        b(e)
                    }), u(window, "focus", function() {
                        v = []
                    }), u(f, "keyup", function(e) {
                        b(e),
                            function(e) {
                                var t = e.keyCode || e.which || e.charCode,
                                    n = v.indexOf(t);
                                if (n < 0 || v.splice(n, 1), e.key && "meta" == e.key.toLowerCase() && v.splice(0, v.length), (t = 93 === t || 224 === t ? 91 : t) in m)
                                    for (var o in m[t] = !1, y)
                                        y[o] === t && (x[o] = !1)
                            }(e)
                    }))
                }
                var i,
                    a,
                    c = {
                        setScope: r,
                        getScope: O,
                        deleteScope: function(e, t) {
                            var n,
                                o,
                                i;
                            for (i in e = e || O(), g)
                                if (Object.prototype.hasOwnProperty.call(g, i))
                                    for (n = g[i], o = 0; o < n.length;)
                                        n[o].scope === e ? n.splice(o, 1) : o++;
                            O() === e && r(t || "all")
                        },
                        getPressedKeyCodes: function() {
                            return v.slice(0)
                        },
                        isPressed: function(e) {
                            return "string" == typeof e && (e = k(e)), !!~v.indexOf(e)
                        },
                        filter: function(e) {
                            var t = e.target || e.srcElement,
                                e = t.tagName;
                            return !t.isContentEditable && ("INPUT" !== e && "TEXTAREA" !== e && "SELECT" !== e || t.readOnly) ? !0 : !1
                        },
                        unbind: function(e) {
                            if (e) {
                                if (Array.isArray(e))
                                    e.forEach(function(e) {
                                        e.key && f(e)
                                    });
                                else if ("object" == typeof e)
                                    e.key && f(e);
                                else if ("string" == typeof e) {
                                    for (var t = arguments.length, n = Array(1 < t ? t - 1 : 0), o = 1; o < t; o++)
                                        n[o - 1] = arguments[o];
                                    var i = n[0],
                                        r = n[1];
                                    "function" == typeof i && (r = i, i = ""),
                                        f({
                                            key: e,
                                            scope: i,
                                            method: r,
                                            splitKey: "+"
                                        })
                                }
                            } else
                                Object.keys(g).forEach(function(e) {
                                    return delete g[e]
                                })
                        }
                    };
                for (i in c)
                    Object.prototype.hasOwnProperty.call(c, i) && (x[i] = c[i]);
                return "undefined" != typeof window && (a = window.hotkeys, x.noConflict = function(e) {
                    return e && window.hotkeys === x && (window.hotkeys = a), x
                }, window.hotkeys = x), x
            });
            ;
            window.hotkeys('command+option+i', () => {
                window.__TAURI_INVOKE__('tauri', {
                    __tauriModule: 'Window',
                    message: {
                        cmd: 'manage',
                        data: {
                            cmd: {
                                type: '__toggleDevtools'
                            }
                        }
                    }
                });
            });

        })()





        ;
        // Copyright 2019-2022 Tauri Programme within The Commons Conservancy
        // SPDX-License-Identifier: Apache-2.0
        // SPDX-License-Identifier: MIT
        (function() {
            function __tauriDeepFreeze(object) {
                const props = Object.getOwnPropertyNames(object)

                for (const prop of props) {
                    if (typeof object[prop] === 'object') {
                        __tauriDeepFreeze(object[prop])
                    }
                }

                return Object.freeze(object)
            }

            Object.defineProperty(window, '__TAURI_PATTERN__', {
                value: __tauriDeepFreeze(JSON.parse('{"pattern":"brownfield"}'))
            })
        })()










        ;
        // Copyright 2019-2022 Tauri Programme within The Commons Conservancy
        // SPDX-License-Identifier: Apache-2.0
        // SPDX-License-Identifier: MIT
        /**
         * @typedef {{callback: string, error: string, data: *}} IsolationPayload - a valid isolation payload
         */
        (function() {
            /**
             * @type {string}
             */
            const pattern = window.__TAURI_PATTERN__.pattern
            /**
             * @type {string}
             */
            const isolationOrigin = JSON.parse('""')
            /**
             * @type {{queue: object[], ready: boolean, frame: HTMLElement | null}}
             */
            const isolation = Object.create(null)
            isolation.queue = []
            isolation.ready = false
            isolation.frame = null
            /**
             * Detects if a message event is a valid isolation message.
             *
             * @param {MessageEvent<object>} event - a message event that is expected to be an isolation message
             * @return {boolean} - if the event was a valid isolation message
             */
            function isIsolationMessage(event) {
                return (
                    typeof event.data === 'object' &&
                    'nonce' in event.data &&
                    'payload' in event.data
                )
            }

            /**
             * Detects if data is able to transform into an isolation payload.
             *
             * @param {object} data - object that is expected to contain at least a callback and error identifier
             * @return {boolean} - if the data is able to transform into an isolation payload
             */
            function isIsolationPayload(data) {
                return typeof data === 'object' && 'callback' in data && 'error' in data
            }

            /**
             * Sends a properly formatted message to the isolation frame.
             *
             * @param {IsolationPayload} data - data that has been validated to be an isolation payload
             */
            function sendIsolationMessage(data) {
                // set the frame dom element if it's not been set before
                if (!isolation.frame) {
                    const frame = document.querySelector('iframe#__tauri_isolation__')
                    if (frame.src.startsWith(isolationOrigin)) {
                        isolation.frame = frame
                    } else {
                        console.error(
                            'Tauri IPC found an isolation iframe, but it had the wrong origin'
                        )
                    }
                }

                // ensure we have the target to send the message to
                if (!isolation.frame || !isolation.frame.contentWindow) {
                    console.error(
                        'Tauri "Isolation" Pattern could not find the Isolation iframe window'
                    )
                    return
                }

                isolation.frame.contentWindow.postMessage(
                    data,
                    '*' /* todo: set this to the secure origin */
                )
            }

            Object.defineProperty(window, '__TAURI_IPC__', {
                // todo: JSDoc this function
                value: Object.freeze((message) => {
                    switch (pattern) {
                        case 'brownfield':
                            window.__TAURI_POST_MESSAGE__(message)
                            break

                        case 'isolation':
                            if (!isIsolationPayload(message)) {
                                console.error(
                                    'Tauri "Isolation" Pattern found an invalid isolation message payload',
                                    message
                                )
                                break
                            }

                            if (isolation.ready) {
                                sendIsolationMessage(message)
                            } else {
                                isolation.queue.push(message)
                            }

                            break

                        case 'error':
                            console.error(
                                'Tauri IPC found a Tauri Pattern, but it was an error. Check for other log messages to find the cause.'
                            )
                            break

                        default:
                            console.error(
                                'Tauri IPC did not find a Tauri Pattern that it understood.'
                            )
                            break
                    }
                })
            })
            /**
             * IMPORTANT: See isolation_secure.js for the isolation frame implementation.
             * main frame -> isolation frame = isolation payload
             * isolation frame -> main frame = isolation message
             */
            if (pattern === 'isolation') {
                window.addEventListener(
                    'message',
                    (event) => {
                        // watch for the isolation frame being ready and flush any queued messages
                        if (event.data === '__TAURI_ISOLATION_READY__') {
                            isolation.ready = true

                            for (const message of isolation.queue) {
                                sendIsolationMessage(message)
                            }

                            isolation.queue = []
                            return
                        }

                        if (isIsolationMessage(event)) {
                            window.__TAURI_POST_MESSAGE__(event.data)
                        }
                    },
                    false
                )
            }
        })()

        ;
        (function() {

        })()

        function listen(eventName, cb) {
            (function() {
                if (window['9594d620-70d4-4fd8-81a5-3f94703c613b'] === void 0) {
                    Object.defineProperty(window, '9594d620-70d4-4fd8-81a5-3f94703c613b', {
                        value: Object.create(null)
                    });
                }
                if (window['9594d620-70d4-4fd8-81a5-3f94703c613b'][eventName] === void 0) {
                    Object.defineProperty(window['9594d620-70d4-4fd8-81a5-3f94703c613b'], eventName, {
                        value: []
                    });
                }
                const eventListeners = window['9594d620-70d4-4fd8-81a5-3f94703c613b'][eventName]
                const listener = {
                    id: 0,
                    windowLabel: null,
                    handler: window['_' + window.__TAURI__.transformCallback(cb)]
                };
                if (eventName == 'tauri://window-created') {
                    eventListeners.splice(eventListeners.length - 1, 0, listener)
                } else {
                    eventListeners.push(listener);
                }
            })()
        }

        // Copyright 2019-2022 Tauri Programme within The Commons Conservancy
        // SPDX-License-Identifier: Apache-2.0
        // SPDX-License-Identifier: MIT

        ;
        (function() {
            function uid() {
                return window.crypto.getRandomValues(new Uint32Array(1))[0]
            }

            if (!window.__TAURI__) {
                Object.defineProperty(window, '__TAURI__', {
                    value: {}
                })
            }

            window.__TAURI__.transformCallback = function transformCallback(
                callback,
                once
            ) {
                var identifier = uid()
                var prop = `_${identifier}`

                Object.defineProperty(window, prop, {
                    value: (result) => {
                        if (once) {
                            Reflect.deleteProperty(window, prop)
                        }

                        return callback && callback(result)
                    },
                    writable: false,
                    configurable: true
                })

                return identifier
            }

            const ipcQueue = []
            let isWaitingForIpc = false

            function waitForIpc() {
                if ('__TAURI_IPC__' in window) {
                    for (const action of ipcQueue) {
                        action()
                    }
                } else {
                    setTimeout(waitForIpc, 50)
                }
            }

            window.__TAURI_INVOKE__ = function invoke(cmd, args={}) {
                return new Promise(function(resolve, reject) {
                    var callback = window.__TAURI__.transformCallback(function(r) {
                        resolve(r)
                        delete window[`_${error}`]
                    }, true)
                    var error = window.__TAURI__.transformCallback(function(e) {
                        reject(e)
                        delete window[`_${callback}`]
                    }, true)

                    if (typeof cmd === 'string') {
                        args.cmd = cmd
                    } else if (typeof cmd === 'object') {
                        args = cmd
                    } else {
                        return reject(new Error('Invalid argument type.'))
                    }

                    const action = () => {
                        window.__TAURI_IPC__({
                            ...args,
                            callback,
                            error: error
                        })
                    }
                    if (window.__TAURI_IPC__) {
                        action()
                    } else {
                        ipcQueue.push(action)
                        if (!isWaitingForIpc) {
                            waitForIpc()
                            isWaitingForIpc = true
                        }
                    }
                })
            }
            // open <a href="..."> links with the Tauri API
            function __openLinks() {
                document.querySelector('body').addEventListener(
                    'click',
                    function(e) {
                        var target = e.target
                        while (target != null) {
                            if (target.matches('a')) {
                                if (
                                    target.href &&
                                    target.href.startsWith('http') &&
                                    target.target === '_blank'
                                ) {
                                    window.__TAURI_INVOKE__('tauri', {
                                        __tauriModule: 'Shell',
                                        message: {
                                            cmd: 'open',
                                            path: target.href
                                        }
                                    })
                                    e.preventDefault()
                                }
                                break
                            }
                            target = target.parentElement
                        }
                    }
                )
            }

            if (
                document.readyState === 'complete' ||
                document.readyState === 'interactive'
            ) {
                __openLinks()
            } else {
                window.addEventListener(
                    'DOMContentLoaded',
                    function() {
                        __openLinks()
                    },
                    true
                )
            }

            // drag region
            document.addEventListener('mousedown', (e) => {
                if (e.target.hasAttribute('data-tauri-drag-region') && e.buttons === 1) {
                    // prevents text cursor
                    e.preventDefault()// start dragging if the element has a `tauri-drag-region` data attribute and maximize on double-clicking it
                    window.__TAURI_INVOKE__('tauri', {
                        __tauriModule: 'Window',
                        message: {
                            cmd: 'manage',
                            data: {
                                cmd: {
                                    type: e.detail === 2 ? '__toggleMaximize' : 'startDragging'
                                }
                            }
                        }
                    })
                }
            })

            listen('tauri://window-created', function(event) {
                if (event.payload) {
                    var windowLabel = event.payload.label
                    window.__TAURI_METADATA__.__windows.push({
                        label: windowLabel
                    })
                }
            })

            let permissionSettable = false
            let permissionValue = 'default'

            function isPermissionGranted() {
                if (window.Notification.permission !== 'default') {
                    return Promise.resolve(window.Notification.permission === 'granted')
                }
                return window.__TAURI_INVOKE__('tauri', {
                    __tauriModule: 'Notification',
                    message: {
                        cmd: 'isNotificationPermissionGranted'
                    }
                })
            }

            function setNotificationPermission(value) {
                permissionSettable = true
                window.Notification.permission = value
                permissionSettable = false
            }

            function requestPermission() {
                return window
                    .__TAURI_INVOKE__('tauri', {
                        __tauriModule: 'Notification',
                        message: {
                            cmd: 'requestNotificationPermission'
                        }
                    })
                    .then(function(permission) {
                        setNotificationPermission(permission)
                        return permission
                    })
            }

            function sendNotification(options) {
                if (typeof options === 'object') {
                    Object.freeze(options)
                }

                return window.__TAURI_INVOKE__('tauri', {
                    __tauriModule: 'Notification',
                    message: {
                        cmd: 'notification',
                        options:
                            typeof options === 'string'
                                ? {
                                    title: options
                                }
                                : options
                    }
                })
            }

            window.Notification = function(title, options) {
                var opts = options || {}
                sendNotification(
                    Object.assign(opts, {
                        title: title
                    })
                )
            }

            window.Notification.requestPermission = requestPermission

            Object.defineProperty(window.Notification, 'permission', {
                enumerable: true,
                get: function() {
                    return permissionValue
                },
                set: function(v) {
                    if (!permissionSettable) {
                        throw new Error('Readonly property')
                    }
                    permissionValue = v
                }
            })

            isPermissionGranted().then(function(response) {
                if (response === null) {
                    setNotificationPermission('default')
                } else {
                    setNotificationPermission(response ? 'granted' : 'denied')
                }
            })

            window.alert = function(message) {
                window.__TAURI_INVOKE__('tauri', {
                    __tauriModule: 'Dialog',
                    message: {
                        cmd: 'messageDialog',
                        message: message.toString()
                    }
                })
            }

            window.confirm = function(message) {
                return window.__TAURI_INVOKE__('tauri', {
                    __tauriModule: 'Dialog',
                    message: {
                        cmd: 'confirmDialog',
                        message: message.toString()
                    }
                })
            }
            // window.print works on Linux/Windows; need to use the API on macOS
            if (navigator.userAgent.includes('Mac')) {
                window.print = function() {
                    return window.__TAURI_INVOKE__('tauri', {
                        __tauriModule: 'Window',
                        message: {
                            cmd: 'manage',
                            data: {
                                cmd: {
                                    type: 'print'
                                }
                            }
                        }
                    })
                }
            }
        })()



        Object.defineProperty(window, '18cdc4f9-49a2-4c99-b6aa-2b00f472213d', {
            value: function(eventData) {
                const listeners = (window['9594d620-70d4-4fd8-81a5-3f94703c613b'] && window['9594d620-70d4-4fd8-81a5-3f94703c613b'][eventData.event]) || []

                for (let i = listeners.length - 1; i >= 0; i--) {
                    const listener = listeners[i]
                    if (listener.windowLabel === null || listener.windowLabel === eventData.windowLabel) {
                        eventData.id = listener.id
                        listener.handler(eventData)
                    }
                }
            }
        });


        if (window.ipc) {
            window.__TAURI_INVOKE__('__initialized', {
                url: window.location.href
            })
        } else {
            window.addEventListener('DOMContentLoaded', function() {
                window.__TAURI_INVOKE__('__initialized', {
                    url: window.location.href
                })
            })
        }


    
})()


