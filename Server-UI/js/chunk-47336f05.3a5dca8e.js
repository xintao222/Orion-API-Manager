(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-47336f05"],{"19e7":function(t,e,i){"use strict";i("a481"),i("6b54"),i("87b3");var r=function(t){function e(t,e){return t<<e|t>>>32-e}function i(t,e){var i,r,n,a,o;return n=2147483648&t,a=2147483648&e,i=1073741824&t,r=1073741824&e,o=(1073741823&t)+(1073741823&e),i&r?2147483648^o^n^a:i|r?1073741824&o?3221225472^o^n^a:1073741824^o^n^a:o^n^a}function r(t,e,i){return t&e|~t&i}function n(t,e,i){return t&i|e&~i}function a(t,e,i){return t^e^i}function o(t,e,i){return e^(t|~i)}function s(t,n,a,o,s,l,c){return t=i(t,i(i(r(n,a,o),s),c)),i(e(t,l),n)}function l(t,r,a,o,s,l,c){return t=i(t,i(i(n(r,a,o),s),c)),i(e(t,l),r)}function c(t,r,n,o,s,l,c){return t=i(t,i(i(a(r,n,o),s),c)),i(e(t,l),r)}function d(t,r,n,a,s,l,c){return t=i(t,i(i(o(r,n,a),s),c)),i(e(t,l),r)}function u(t){var e,i=t.length,r=i+8,n=(r-r%64)/64,a=16*(n+1),o=Array(a-1),s=0,l=0;while(l<i)e=(l-l%4)/4,s=l%4*8,o[e]=o[e]|t.charCodeAt(l)<<s,l++;return e=(l-l%4)/4,s=l%4*8,o[e]=o[e]|128<<s,o[a-2]=i<<3,o[a-1]=i>>>29,o}function g(t){var e,i,r="",n="";for(i=0;i<=3;i++)e=t>>>8*i&255,n="0"+e.toString(16),r+=n.substr(n.length-2,2);return r}function f(t){t=t.replace(/\r\n/g,"\n");for(var e="",i=0;i<t.length;i++){var r=t.charCodeAt(i);r<128?e+=String.fromCharCode(r):r>127&&r<2048?(e+=String.fromCharCode(r>>6|192),e+=String.fromCharCode(63&r|128)):(e+=String.fromCharCode(r>>12|224),e+=String.fromCharCode(r>>6&63|128),e+=String.fromCharCode(63&r|128))}return e}var p,h,v,m,w,C,E,x,y,b=Array(),k=7,S=12,F=17,$=22,_=5,V=9,T=14,H=20,z=4,M=11,D=16,I=23,L=6,O=10,P=15,A=21;for(t=f(t),b=u(t),C=1732584193,E=4023233417,x=2562383102,y=271733878,p=0;p<b.length;p+=16)h=C,v=E,m=x,w=y,C=s(C,E,x,y,b[p+0],k,3614090360),y=s(y,C,E,x,b[p+1],S,3905402710),x=s(x,y,C,E,b[p+2],F,606105819),E=s(E,x,y,C,b[p+3],$,3250441966),C=s(C,E,x,y,b[p+4],k,4118548399),y=s(y,C,E,x,b[p+5],S,1200080426),x=s(x,y,C,E,b[p+6],F,2821735955),E=s(E,x,y,C,b[p+7],$,4249261313),C=s(C,E,x,y,b[p+8],k,1770035416),y=s(y,C,E,x,b[p+9],S,2336552879),x=s(x,y,C,E,b[p+10],F,4294925233),E=s(E,x,y,C,b[p+11],$,2304563134),C=s(C,E,x,y,b[p+12],k,1804603682),y=s(y,C,E,x,b[p+13],S,4254626195),x=s(x,y,C,E,b[p+14],F,2792965006),E=s(E,x,y,C,b[p+15],$,1236535329),C=l(C,E,x,y,b[p+1],_,4129170786),y=l(y,C,E,x,b[p+6],V,3225465664),x=l(x,y,C,E,b[p+11],T,643717713),E=l(E,x,y,C,b[p+0],H,3921069994),C=l(C,E,x,y,b[p+5],_,3593408605),y=l(y,C,E,x,b[p+10],V,38016083),x=l(x,y,C,E,b[p+15],T,3634488961),E=l(E,x,y,C,b[p+4],H,3889429448),C=l(C,E,x,y,b[p+9],_,568446438),y=l(y,C,E,x,b[p+14],V,3275163606),x=l(x,y,C,E,b[p+3],T,4107603335),E=l(E,x,y,C,b[p+8],H,1163531501),C=l(C,E,x,y,b[p+13],_,2850285829),y=l(y,C,E,x,b[p+2],V,4243563512),x=l(x,y,C,E,b[p+7],T,1735328473),E=l(E,x,y,C,b[p+12],H,2368359562),C=c(C,E,x,y,b[p+5],z,4294588738),y=c(y,C,E,x,b[p+8],M,2272392833),x=c(x,y,C,E,b[p+11],D,1839030562),E=c(E,x,y,C,b[p+14],I,4259657740),C=c(C,E,x,y,b[p+1],z,2763975236),y=c(y,C,E,x,b[p+4],M,1272893353),x=c(x,y,C,E,b[p+7],D,4139469664),E=c(E,x,y,C,b[p+10],I,3200236656),C=c(C,E,x,y,b[p+13],z,681279174),y=c(y,C,E,x,b[p+0],M,3936430074),x=c(x,y,C,E,b[p+3],D,3572445317),E=c(E,x,y,C,b[p+6],I,76029189),C=c(C,E,x,y,b[p+9],z,3654602809),y=c(y,C,E,x,b[p+12],M,3873151461),x=c(x,y,C,E,b[p+15],D,530742520),E=c(E,x,y,C,b[p+2],I,3299628645),C=d(C,E,x,y,b[p+0],L,4096336452),y=d(y,C,E,x,b[p+7],O,1126891415),x=d(x,y,C,E,b[p+14],P,2878612391),E=d(E,x,y,C,b[p+5],A,4237533241),C=d(C,E,x,y,b[p+12],L,1700485571),y=d(y,C,E,x,b[p+3],O,2399980690),x=d(x,y,C,E,b[p+10],P,4293915773),E=d(E,x,y,C,b[p+1],A,2240044497),C=d(C,E,x,y,b[p+8],L,1873313359),y=d(y,C,E,x,b[p+15],O,4264355552),x=d(x,y,C,E,b[p+6],P,2734768916),E=d(E,x,y,C,b[p+13],A,1309151649),C=d(C,E,x,y,b[p+4],L,4149444226),y=d(y,C,E,x,b[p+11],O,3174756917),x=d(x,y,C,E,b[p+2],P,718787259),E=d(E,x,y,C,b[p+9],A,3951481745),C=i(C,h),E=i(E,v),x=i(x,m),y=i(y,w);var R=g(C)+g(E)+g(x)+g(y);return R.toLowerCase()};e["a"]=r},"1b46":function(t,e,i){},"2a54":function(t,e,i){"use strict";var r=i("1b46"),n=i.n(r);n.a},3846:function(t,e,i){i("9e1e")&&"g"!=/./g.flags&&i("86cc").f(RegExp.prototype,"flags",{configurable:!0,get:i("0bfb")})},"6b54":function(t,e,i){"use strict";i("3846");var r=i("cb7c"),n=i("0bfb"),a=i("9e1e"),o="toString",s=/./[o],l=function(t){i("2aba")(RegExp.prototype,o,t,!0)};i("79e5")((function(){return"/a/b"!=s.call({source:"a",flags:"b"})}))?l((function(){var t=r(this);return"/".concat(t.source,"/","flags"in t?t.flags:!a&&t instanceof RegExp?n.call(t):void 0)})):s.name!=o&&l((function(){return s.call(this)}))},"6ba9":function(t,e,i){"use strict";var r=i("ce0d"),n=i.n(r);n.a},"87b3":function(t,e,i){var r=Date.prototype,n="Invalid Date",a="toString",o=r[a],s=r.getTime;new Date(NaN)+""!=n&&i("2aba")(r,a,(function(){var t=s.call(this);return t===t?o.call(this):n}))},a5bf:function(t,e,i){"use strict";var r=i("eec5"),n=i.n(r);n.a},c2cf:function(t,e,i){"use strict";i.d(e,"a",(function(){return n}));var r=i("a5f4");function n(t,e){r["a"].get("/config/console").then(t).catch(e)}},ce0d:function(t,e,i){},ede4:function(t,e,i){"use strict";i.r(e);var r=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"login-container"},[i("el-form",{ref:"loginForm",staticClass:"login-form",attrs:{model:t.loginForm,rules:t.rules}},[i("div",{staticClass:"title-container"},[i("h3",{staticClass:"title"},[t._v(t._s(t.welcome))])]),i("el-form-item",{attrs:{prop:"id"}},[i("span",{staticClass:"svg-container"},[i("svg",{staticClass:"icon",attrs:{t:"1594903948207",viewBox:"0 0 1024 1024",version:"1.1",xmlns:"http://www.w3.org/2000/svg","p-id":"4053",width:"16",height:"16"}},[i("path",{attrs:{d:"M858.5 763.6c-18.9-44.8-46.1-85-80.6-119.5-34.5-34.5-74.7-61.6-119.5-80.6-0.4-0.2-0.8-0.3-1.2-0.5C719.5 518 760 444.7 760 362c0-137-111-248-248-248S264 225 264 362c0 82.7 40.5 156 102.8 201.1-0.4 0.2-0.8 0.3-1.2 0.5-44.8 18.9-85 46-119.5 80.6-34.5 34.5-61.6 74.7-80.6 119.5C146.9 807.5 137 854 136 901.8c-0.1 4.5 3.5 8.2 8 8.2h60c4.4 0 7.9-3.5 8-7.8 2-77.2 33-149.5 87.8-204.3 56.7-56.7 132-87.9 212.2-87.9s155.5 31.2 212.2 87.9C779 752.7 810 825 812 902.2c0.1 4.4 3.6 7.8 8 7.8h60c4.5 0 8.1-3.7 8-8.2-1-47.8-10.9-94.3-29.5-138.2zM512 534c-45.9 0-89.1-17.9-121.6-50.4S340 407.9 340 362c0-45.9 17.9-89.1 50.4-121.6S466.1 190 512 190s89.1 17.9 121.6 50.4S684 316.1 684 362c0 45.9-17.9 89.1-50.4 121.6S557.9 534 512 534z","p-id":"4054",fill:"#EEEEEE"}})])]),i("el-input",{ref:"id",attrs:{placeholder:t.$t("EnterAccount"),type:"text",tabindex:"1",autocomplete:"off"},model:{value:t.loginForm.id,callback:function(e){t.$set(t.loginForm,"id",e)},expression:"loginForm.id"}})],1),i("el-form-item",{attrs:{prop:"password"}},[i("span",{staticClass:"svg-container"},[i("svg",{staticClass:"icon",attrs:{t:"1594903789026",viewBox:"0 0 1024 1024",version:"1.1",xmlns:"http://www.w3.org/2000/svg","p-id":"3765",width:"16",height:"16"}},[i("path",{attrs:{d:"M832 464h-68V240c0-70.7-57.3-128-128-128H388c-70.7 0-128 57.3-128 128v224h-68c-17.7 0-32 14.3-32 32v384c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V496c0-17.7-14.3-32-32-32zM332 240c0-30.9 25.1-56 56-56h248c30.9 0 56 25.1 56 56v224H332V240z m460 600H232V536h560v304z","p-id":"3766",fill:"#EEEEEE"}}),i("path",{attrs:{d:"M484 701v53c0 4.4 3.6 8 8 8h40c4.4 0 8-3.6 8-8v-53c12.1-8.7 20-22.9 20-39 0-26.5-21.5-48-48-48s-48 21.5-48 48c0 16.1 7.9 30.3 20 39z","p-id":"3767",fill:"#EEEEEE"}})])]),i("el-input",{key:t.passwordType,ref:"password",attrs:{type:t.passwordType,placeholder:t.$t("EnterPassword"),tabindex:"2",autocomplete:"off"},nativeOn:{keyup:function(e){return!e.type.indexOf("key")&&t._k(e.keyCode,"enter",13,e.key,"Enter")?null:t.loginHandler(e)}},model:{value:t.loginForm.password,callback:function(e){t.$set(t.loginForm,"password",e)},expression:"loginForm.password"}}),i("span",{staticClass:"show-pwd",on:{click:t.showPwd}},[i("svg",{directives:[{name:"show",rawName:"v-show",value:"password"===t.passwordType,expression:"passwordType === 'password'"}],staticClass:"icon",attrs:{t:"1594906186816",viewBox:"0 0 1024 1024",version:"1.1",xmlns:"http://www.w3.org/2000/svg","p-id":"4292",width:"16",height:"16"}},[i("path",{attrs:{d:"M942.2 486.2C847.4 286.5 704.1 186 512 186c-192.2 0-335.4 100.5-430.2 300.3-7.7 16.2-7.7 35.2 0 51.5C176.6 737.5 319.9 838 512 838c192.2 0 335.4-100.5 430.2-300.3 7.7-16.2 7.7-35 0-51.5zM512 766c-161.3 0-279.4-81.8-362.7-254C232.6 339.8 350.7 258 512 258c161.3 0 279.4 81.8 362.7 254C791.5 684.2 673.4 766 512 766z","p-id":"4293",fill:"#EEEEEE"}}),i("path",{attrs:{d:"M508 336c-97.2 0-176 78.8-176 176s78.8 176 176 176 176-78.8 176-176-78.8-176-176-176z m0 288c-61.9 0-112-50.1-112-112s50.1-112 112-112 112 50.1 112 112-50.1 112-112 112z","p-id":"4294",fill:"#EEEEEE"}})]),i("svg",{directives:[{name:"show",rawName:"v-show",value:"password"!==t.passwordType,expression:"passwordType !== 'password'"}],staticClass:"icon",attrs:{t:"1594906203171",viewBox:"0 0 1024 1024",version:"1.1",xmlns:"http://www.w3.org/2000/svg","p-id":"4527",width:"16",height:"16"}},[i("path",{attrs:{d:"M942.3 486.4l-0.1-0.1-0.1-0.1c-36.4-76.7-80-138.7-130.7-186L760.7 351c43.7 40.2 81.5 93.7 114.1 160.9C791.5 684.2 673.4 766 512 766c-51.3 0-98.3-8.3-141.2-25.1l-54.7 54.7C374.6 823.8 439.8 838 512 838c192.2 0 335.4-100.5 430.2-300.3 7.7-16.2 7.7-35 0.1-51.3z m-64-332.2l-42.4-42.4c-3.1-3.1-8.2-3.1-11.3 0L707.8 228.5C649.4 200.2 584.2 186 512 186c-192.2 0-335.4 100.5-430.2 300.3v0.1c-7.7 16.2-7.7 35.2 0 51.5 36.4 76.7 80 138.7 130.7 186.1L111.8 824.5c-3.1 3.1-3.1 8.2 0 11.3l42.4 42.4c3.1 3.1 8.2 3.1 11.3 0l712.8-712.8c3.1-3 3.1-8.1 0-11.2zM398.9 537.4c-1.9-8.2-2.9-16.7-2.9-25.4 0-61.9 50.1-112 112-112 8.7 0 17.3 1 25.4 2.9L398.9 537.4z m184.5-184.5C560.5 342.1 535 336 508 336c-97.2 0-176 78.8-176 176 0 27 6.1 52.5 16.9 75.4L263.3 673c-43.7-40.2-81.5-93.7-114.1-160.9C232.6 339.8 350.7 258 512 258c51.3 0 98.3 8.3 141.2 25.1l-69.8 69.8z","p-id":"4528",fill:"#EEEEEE"}}),i("path",{attrs:{d:"M508 624c-6.4 0-12.7-0.5-18.8-1.6l-51.1 51.1c21.4 9.3 45.1 14.4 69.9 14.4 97.2 0 176-78.8 176-176 0-24.8-5.1-48.5-14.4-69.9l-51.1 51.1c1 6.1 1.6 12.4 1.6 18.8C620 573.9 569.9 624 508 624z","p-id":"4529",fill:"#EEEEEE"}})])])],1),i("el-form-item",[i("div",{staticStyle:{display:"flex","justify-content":"right",padding:"3px 10px"}},[i("Verification",{ref:"verification",attrs:{tipsColor:"#C0C4CC",resetColor:"#EEE",dataUrl:t.exportServerHost+"/verification/data",imgPrefix:t.exportServerHost+"/verification/img/",loadDataErrHandler:t.verifyErr,clickFinishHandler:t.verifyFinish}})],1)]),i("p",{directives:[{name:"show",rawName:"v-show",value:t.loginError,expression:"loginError"}],staticStyle:{"text-align":"center",color:"#F56C6C","font-size":"14px"}},[t._v(t._s(t.loginFailedTips))]),i("el-button",{staticStyle:{width:"100%","margin-bottom":"30px"},attrs:{loading:t.loading,type:"primary"},nativeOn:{click:function(e){return e.preventDefault(),t.loginCheckHandler(e)}}},[t._v(t._s(t.$t("Login")))])],1),t._m(0)],1)},n=[function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticStyle:{"z-index":"2002",position:"fixed",bottom:"0",right:"0",padding:"20px",cursor:"pointer"}},[i("a",{staticStyle:{color:"white","text-decoration":"none"},attrs:{href:"https://mirren.gitee.io/orion-api-manager-docs/",target:"_blank",title:"MirrenTools.org developed program Orion-API-Manager"}},[t._v("\n\t\t\tPowered by OAM\n\t\t")])])}],a=i("19e7"),o=i("b0af"),s=i("c2cf"),l=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticStyle:{display:"flex","flex-wrap":"wrap"}},[i("div",{staticClass:"captcha-tips",style:{color:t.tipsColor}},[t._v(t._s(t.$t("SelectCaptchaInOrder"))+t._s(t.imgData))]),i("div",{staticStyle:{display:"flex"}},[i("div",{staticClass:"captcha-img",style:{backgroundImage:"url("+t.imgUrl+")"}},t._l(t.imgValues,(function(e,r){return i("div",{key:r,class:["captcha-num",{"captcha-clicked":e.isCheck}],on:{click:function(e){return t.click(r)}}})})),0),i("div",{staticClass:"captcha-reset",style:{color:t.resetColor},on:{click:t.reset}},[i("i",{staticClass:"el-icon-refresh"})])])])},c=[],d=i("bc3a"),u=i.n(d),g={data:function(){return{imgData:"",imgUrl:"",imgIndex:"",indexVal:"",imgValues:[{value:"00",isCheck:!1},{value:"01",isCheck:!1},{value:"02",isCheck:!1},{value:"03",isCheck:!1},{value:"04",isCheck:!1}]}},props:{dataUrl:{type:String,required:!0},tipsColor:String,resetColor:{type:String,default:"#007bff"},imgPrefix:String,imgSuffix:String,loadDataErrHandler:Function,clickFinishHandler:Function},methods:{click:function(t){this.imgValues[t].isCheck||8===this.indexVal.length||(this.imgValues[t].isCheck=!0,this.indexVal=this.indexVal+=this.imgValues[t].value,8===this.indexVal.length&&this.clickFinishHandler(this.imgIndex,this.indexVal))},reset:function(){this.imgValues=[{value:"00",isCheck:!1},{value:"01",isCheck:!1},{value:"02",isCheck:!1},{value:"03",isCheck:!1},{value:"04",isCheck:!1}],this.indexVal="",this.imgData="",this.initImageData()},initImageData:function(){var t=this;u.a.get(t.dataUrl).then((function(e){t.imgIndex=e.data.index,t.imgData=e.data.data,t.imgUrl=(t.imgPrefix||"")+e.data.code+(t.imgSuffix||"")})).catch((function(e){t.loadDataErrHandler?t.loadDataErrHandler(e):console.log(e)}))}},mounted:function(){this.initImageData()}},f=g,p=(i("2a54"),i("2877")),h=Object(p["a"])(f,l,c,!1,null,null,null),v=h.exports,m={components:{Verification:v},data:function(){var t=this,e=function(e,i,r){i.length<4||i.length>32?r(new Error(t.$t("AccountRequires_4_32"))):r()},i=function(e,i,r){i.length<4||i.length>32?r(new Error(t.$t("PasswordRequires_4_32"))):r()};return{exportServerHost:"",welcome:"Welcome to Orion-API-Manager",verifyIndex:"",verifyValue:"",loginForm:{id:"",password:""},rules:{id:[{validator:e,trigger:"blur"}],password:[{validator:i,trigger:"blur"}]},passwordType:"password",loading:!1,loginError:!1,loginFailedTips:this.$t("LoginFailed")}},created:function(){var t=this;Object(s["a"])((function(e){var i=e.data;console.log("get console config: ",i),200==i.code&&(document.title=i.data.title,t.welcome=i.data.welcome)}),(function(t){console.log("get console config failed: ",t)}))},methods:{showPwd:function(){var t=this;"password"===this.passwordType?this.passwordType="":this.passwordType="password",this.$nextTick((function(){t.$refs.password.focus()}))},verifyErr:function(t){console.log(t)},verifyFinish:function(t,e){this.verifyIndex=t,this.verifyValue=e,""!=this.loginForm.id&&""!=this.loginForm.password&&this.loginCheckHandler()},loginCheckHandler:function(){this.loginHandler()},loginHandler:function(){var t=this;this.loading=!0,this.loginError=!1,this.$refs.loginForm.validate((function(e){if(e){if(8!=t.verifyValue.length)return t.loginError=!0,t.loginFailedTips=t.$t("PleaseSelectCaptcha"),void(t.loading=!1);var i={id:t.loginForm.id,pwd:Object(a["a"])(t.loginForm.password),index:t.verifyIndex,value:t.verifyValue};Object(o["a"])(i,(function(e){t.loading=!1;var i=e.data;if(200==i.code){var r=i.data;t.$store.dispatch("app/login",r),console.log("Login successful!"),"X-root"==t.loginForm.id&&"helloOAM"==t.loginForm.password&&t.$alert(t.$t("PleaseModifyTheLoginPassword"),t.$t("Tips"),{confirmButtonText:t.$t("Confirm"),callback:function(e){t.$router.push("/index")}})}else 1304==i.code?(console.log("Login failed:"+i.msg),t.loginError=!0,t.loginFailedTips=t.$t("ResultStatus1304"),t.$refs.verification.reset()):(console.log("Login failed:"+i.msg),t.loginError=!0,t.loginFailedTips=t.$t("LoginFailed"),t.$refs.verification.reset())}),(function(e){t.loading=!1;var i=t.$t("RequestFailedSeeConsole");t.$message.error(i),t.loginError=!0,t.loginFailedTips=i,console.log("Request failed!"),console.log(e)}))}else t.loading=!1}))}}},w=m,C=(i("6ba9"),i("a5bf"),Object(p["a"])(w,r,n,!1,null,"72230204",null));e["default"]=C.exports},eec5:function(t,e,i){}}]);
//# sourceMappingURL=chunk-47336f05.3a5dca8e.js.map