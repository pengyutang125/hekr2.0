;(function() {
	if (window.WebViewJavascriptBridge) {
		return;
	}
	console.log('first------------');
	var messageHandlers = {};
	var responseCallbacks = {};
	var uniqueId = 1;

	function init(messageHandler) {
		if (WebViewJavascriptBridge._messageHandler) { throw new Error('WebViewJavascriptBridge.init called twice'); }
		WebViewJavascriptBridge._messageHandler = messageHandler;
	}

	function send(data, responseCallback) {
		_doSend({ data:data }, responseCallback);
	}

	function registerHandler(handlerName, handler) {
		messageHandlers[handlerName] = handler;
	}

	function callHandler(handlerName, data, responseCallback) {
		_doSend({ handlerName:handlerName, data:data }, responseCallback);
	}

	function _doSend(message, responseCallback) {
		if (responseCallback) {
			var callbackId = 'cb_'+(uniqueId++)+'_'+new Date().getTime();
			responseCallbacks[callbackId] = responseCallback;
			message['callbackId'] = callbackId;
		}
		_WebViewJavascriptBridge._handleMessageFromJs(JSON.stringify(message.data)||null,message.responseId||null,
		message.responseData||null,message.callbackId||null,message.handlerName||null);

	}

	function _dispatchMessageFromJava(messageJSON) {
		var message = JSON.parse(messageJSON);
		var messageHandler;

		if (message.responseId) {
			var responseCallback = responseCallbacks[message.responseId];
			if (!responseCallback) { return; }
			responseCallback(message.responseData);
			delete responseCallbacks[message.responseId];
		} else {
			var responseCallback;
			if (message.callbackId) {
				var callbackResponseId = message.callbackId;
				responseCallback = function(responseData) {
					_doSend({ responseId:callbackResponseId, responseData:responseData });
				}
			}

			var handler = WebViewJavascriptBridge._messageHandler;
			if (message.handlerName) {
				handler = messageHandlers[message.handlerName];
			}
			try {
				handler(message.data, responseCallback);
			} catch(exception) {
				if (typeof console != 'undefined') {
					console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
					console.log(message.data);
					console.log(exception);
				}
			}
		}
	}


	function _handleMessageFromJava(messageJSON) {
		_dispatchMessageFromJava(messageJSON);
	}

	function _initHekrSDK(bridge) {
		function _getDevices(callback){
			bridge.callHandler('getDevices',{},function(res){
				callback(res.obj,res.error);
			});
		}
		function _sendMsg(tid,msg,type){
			bridge.callHandler('sendMsg',{'tid':tid,'msg':msg,'type':type},function(ret){

			});
		}
		function _setMsgHandle(tid,handle){
			window.Hekr.messageHandels[tid] = handle;
		}
		function _renameDevice(tid,name,callback){
			bridge.callHandler('renameDevice',{'tid':tid,"name":name},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _setGroup(tid,gid,name,callback){
			bridge.callHandler('setGroup',{'tid':tid,'name':name,'gid':gid},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _getGroups(callback){
			bridge.callHandler('getGroups',{},function(res){
				callback(res.obj,res.error);
			});
		}
		function _createGroup(name,callback){
			bridge.callHandler('createGroup',{'name':name},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _removeGroup(gid,callback){
			bridge.callHandler('removeGroup',{'gid':gid},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _renameGroup(gid,name,callback){
			bridge.callHandler('renameGroup',{'gid':gid,'name':name},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _config(ssid,pwd,callback){
			bridge.callHandler('config',{'ssid':ssid,'pwd':pwd},function(ret){
				callback(ret.obj,ret.error);
			});
		}
		function _isConnectSoftAP(callback){
			bridge.callHandler('isConnectSoftAP',{},function(ret){
				callback(ret);
			});
		}
		function _getAPList(callback){
			bridge.callHandler('getAPList',{},function(res){
				callback(res.obj,res.error);
			});
		}
		function _cancelConfig(){
			bridge.callHandler('cancelConfig',{},function(ret){

			});
		}
		function _currentUser(callback){
			bridge.callHandler('currentUser',{},function(ret){
				callback(ret.obj);
			});
		}
		function _logout(){
			bridge.callHandler('logout',{},function(ret){
			});
		}
		function _onUser(callback){
              window.Hekr.userHandel = callback;
            }
		function _onRemoteNotification(callback){
			window.Hekr.remoteNotificationHandle = callback;
			bridge.callHandler('remoteNotificationsReady',{},function(ret){
			});
		}
		function _defaultRemoteNotificationHandle(notification){

		}
		function _defaultUserHandel(user){
		}
		function _close(animation){
			bridge.callHandler('close',{'animation':animation},function(ret){
			});
		}
		function _closeAll(animation){
			bridge.callHandler('closeAll',{'animation':animation},function(ret){
			});
		}
		function _currentSSID(callback){
			bridge.callHandler('currentSSID',{},function(res){
				callback(res.obj);
			});
		}
		window.Hekr = {
			messageHandels:{},
			userHandel:_defaultUserHandel,
			remoteNotificationHandle:_defaultRemoteNotificationHandle,
			getDevices:_getDevices,
			sendMsg:_sendMsg,
			setMsgHandle:_setMsgHandle,
			renameDevice:_renameDevice,
			setGroup:_setGroup,
			getGroups:_getGroups,
			createGroup:_createGroup,
			renameGroup:_renameGroup,
			removeGroup:_removeGroup,
			config:_config,
			isConnectSoftAP:_isConnectSoftAP,
			getAPList:_getAPList,
			cancelConfig:_cancelConfig,
			currentUser:_currentUser,
			logout:_logout,
			onUser:_onUser,
			close:_close,
			closeAll:_closeAll,
			currentSSID:_currentSSID,
			onRemoteNotification:_onRemoteNotification
		}
		bridge.init(function(message, responseCallback) {
			responseCallback({});
		})
		bridge.registerHandler('onMessage',function(data,callback){
			var tid = data.tid;
			var msg = data.msg;
			if (Hekr.messageHandels.hasOwnProperty(tid)) {
				Hekr.messageHandels[tid](msg);
			}
			callback({});
		})
		bridge.registerHandler('onUserChange',function(data,callback){
			Hekr.userHandel(data);
			callback({});
		})
	}

	//export
	window.WebViewJavascriptBridge = {
		init: init,
		send: send,
		registerHandler: registerHandler,
		callHandler: callHandler,
		_handleMessageFromJava: _handleMessageFromJava
	}

	//_initHekrSDK(WebViewJavascriptBridge);

	//dispatch event
	var doc = document;
	var readyEvent = doc.createEvent('Events');
	readyEvent.initEvent('HekrSDKReady');
	readyEvent.bridge = WebViewJavascriptBridge;
	doc.dispatchEvent(readyEvent);
	console.log('end------------');
})();
