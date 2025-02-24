package extension;

import dao.IDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("dao2")
@Primary
public class DaoImplV2 implements IDao {
    @Override
    public double getData() {
        System.out.println("Web Service version");
        return 12;
    }
}
