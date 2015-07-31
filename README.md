# vertx-pojo-mapper

map POJO to datastores and back

Simple example to write and read a pojo ( as JUnit test )
Run it inside the subproject vertx-pojongo


  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  @Test
  public void Demo() {
    try {
      /*
       * Init a MongoClient onto a locally running Mongo
       */
      JsonObject config = new JsonObject();
      config.put("connection_string", "mongodb://localhost:27017");
      config.put("db_name", "PojongoTestDatabase");
      mongoClient = MongoClient.createNonShared(vertx, config);
      mongoDataStore = new MongoDataStore(mongoClient);

      DemoMapper dm = new DemoMapper();
      dm.setName("demoMapper");
      DemoSubMapper dmsr = new DemoSubMapper();
      dmsr.subname = "referenced submapper";
      dm.subMapperReferenced = dmsr;

      DemoSubMapper dmse = new DemoSubMapper();
      dmse.subname = "referenced submapper";
      dm.subMapperEmbedded = dmse;

      IWrite<DemoMapper> write = mongoDataStore.createWrite(DemoMapper.class);
      write.add(dm);
      write.save(result -> {
        if (result.failed()) {
          logger.error(result.cause());
          fail(result.cause().getMessage());
        } else {
          IWriteResult wr = result.result();
          IWriteEntry entry = wr.iterator().next();
          logger.info("written with id " + entry.getId());
          logger.info("written action: " + entry.getAction());
          logger.info("written as " + entry.getStoreObject());

          IQuery<DemoMapper> query = mongoDataStore.createQuery(DemoMapper.class);
          query.field("name").is("demoMapper");
          query.execute(rResult -> {
            if (rResult.failed()) {
              logger.error(rResult.cause());
              fail(rResult.cause().getMessage());
            } else {
              IQueryResult<DemoMapper> qr = rResult.result();
              qr.iterator().next(itResult -> {
                if (itResult.failed()) {
                  logger.error(itResult.cause());
                  fail(itResult.cause().getMessage());
                } else {
                  DemoMapper readMapper = itResult.result();
                  logger.info("id " + readMapper.id);

                }
              });
            }
          });

        }
      });
    } finally {
      if (mongoClient != null)
        mongoClient.close();
    }

  }

  public class DemoMapper {
    @Id
    public String id;
    private String name;
    @Embedded
    public DemoSubMapper subMapperEmbedded;
    @Referenced
    public DemoSubMapper subMapperReferenced;

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * @param name
     *          the name to set
     */
    public void setName(String name) {
      this.name = name;
    }

  }

  public class DemoSubMapper {
    @Id
    public String id;
    public String subname;

  }
