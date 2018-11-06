
        	var m={
				action:"Tst",
				params:'ok'

			};
			var Local={
				call:function(json){
              },
				execute:function(action,json,func){
					m.params=json;
					m.action=action;
					this.call=func;
					LocalNative.execute(JSON.stringify(m));

				}

			}