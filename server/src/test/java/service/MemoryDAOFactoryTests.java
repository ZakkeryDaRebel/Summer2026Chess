package service;

import dataaccess.DAOFactory;
import dataaccess.MemoryDAOFactory;

public class MemoryDAOFactoryTests extends UserServiceTests {

    protected DAOFactory createFactory() {
        return new MemoryDAOFactory();
    }
}
