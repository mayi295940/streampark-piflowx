package cn.piflow.conf

import cn.piflow.StreamingStop

abstract class ConfigurableStreamingStop[StreamingContext, DataType, DStream]
  extends ConfigurableStop[DataType]
  with StreamingStop[StreamingContext, DataType, DStream] {}
