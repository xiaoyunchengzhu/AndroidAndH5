# AndroidAndH5
安卓H5交互,独立规则。
     1，在交互规则里，js调用安卓，统一传一个action,作为标记，表明要执行的行动中的特定方法，然后传入参数，json格式的参数，
  最后一个是回调方法。可以匿名实现回调方法。非常的方便。js调用安卓只需要一个方法，就能保证传入参数，action,还有回调方法。
     2，安卓端，要声明方法实现。只需要在service包中建立一个继承抽象类ActionService的类。重写和实现里面的方法。
  同时也对应着json字符串转对象的方法，或者不转对象，直接返回字符串。把类名作为字符串的action发给js端，把参数对象的格式也发给js端，
  js就按照这个方式去像1，中一样去调用安卓本地方法。
  import android.app.Activity;

/**
 * Created by TL on 2016/6/1.
 */
public abstract class ActionService<T> {

      protected Activity activity;
      public ActionService(Activity activity)
      {
            this.activity=activity;
      }
      public  abstract void execute(T param,CallBackJs callBackJs);
      public abstract   T fromJson(String params);

}

一个execute方法是js调用本地的方法，需要实现。参数param就是被fromJson转化来的参数。CallBackJs就是回调给js的方法。在实现execute中
可以调用回调方法，也可以不调用这个回调方法给jS.

例如js端执行这个方法。

  var TestBean={
				name:"lili",
				sex:"男",
				age:12
			};
Local.execute('Test',TestBean,function(json){
			alert(json);
		})
		
		传入action Test,TestBean参数。然后是匿名实现的回调方法。安卓中执行CallbackJs的回调，fucntion（json）{ alert(json);}就会执行。
		
		大大方便了安卓端的逻辑，还有js端的调用。
