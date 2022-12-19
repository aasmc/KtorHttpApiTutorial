package aasmc.ru.playground.applications.appmodel.dao

import aasmc.ru.playground.applications.appmodel.model.Bid

class BidDAOImpl : GenericDaoImpl<Bid, Long>(Bid::class.java), BidDAO {
}