package co.makery.example.requery

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.query.Result
import io.requery.rx.RxSupport
import io.requery.sql.EntityDataStore
import kotlinx.android.synthetic.main.activity_main.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : RxAppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val source = DatabaseSource(this, Models.DEFAULT, 1);
    val dataStore = RxSupport.toReactiveStore(
      EntityDataStore<Persistable>(source.configuration));

    b_insert_button.setOnClickListener {
      val user = UserEntity()
      user.name = et_user_name.text.toString();

      dataStore.insert(user)
        .toObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindToLifecycle<UserEntity>())
        .subscribe { userEntity -> Log.d("MainActivity", "User added: " + userEntity.getName()) }
    }

    dataStore.select(UserEntity::class.java)
      .get()
      .toSelfObservable()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .compose(bindToLifecycle<Result<UserEntity>>())
      .subscribe { result -> updateUserList(result.toList()) }
  }

  private fun updateUserList(userEntityList: List<UserEntity>) {
    ll_db_content.removeAllViews()
    userEntityList.forEach { userEntity ->
      ll_db_content.addView(TextView(this).apply {
        text = userEntity.getId().toString() + " " + userEntity.getName()
      })
    }
  }
}