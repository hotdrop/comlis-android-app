package jp.hotdrop.compl;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.hotdrop.compl.dao.CompanyDao;
import jp.hotdrop.compl.model.Company;

/**
 * Created by kenji on 2017/02/17.
 */

public class RxTest {

    public void test() {
        CompanyDao dao = CompanyDao.INSTANCE;
        Disposable disp = dao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLoadSuccess,
                        throwable -> onError(throwable)
                );
    }

    private void onLoadSuccess(List<Company> companies) {

    }

    private void onError(Throwable throwable) {

    }
}
