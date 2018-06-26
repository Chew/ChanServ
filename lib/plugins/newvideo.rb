module NewVideo
  extend Discordrb::Commands::CommandContainer

  command(:newvideo, in: 445_763_505_577_132_032, min_args: 1) do |event, videourl|
    event.message.delete
    url = videourl.split(/[\/,&,?,=]/)
    case url[2]
    when 'www.youtube.com'
      id = url[5]
    when 'youtu.be'
      id = url[3]
    else
      break
    end
    url = JSON.parse(RestClient.get("https://www.googleapis.com/youtube/v3/videos?id=#{id}&key=#{CONFIG['google']}&part=snippet,contentDetails,statistics"))

    info = url['items'][0]['snippet']
    length = url['items'][0]['contentDetails']['duration']
    length = length[2..length.length]
    length.downcase!
    upload = Time.parse(info['publishedAt'])
    upload = upload.split('-')
    urlpls = "http://youtu.be/#{id}"

    event.channel.send_embed do |e|
      e.title = 'New Video!'

      e.timestamp = upload[1]

      e.add_field(name: 'Title', value: info['title'], inline: true)
      e.add_field(name: 'Channel', value: info['channelTitle'], inline: true)
      e.add_field(name: 'Duration', value: length, inline: true)

      e.url = urlpls

      e.color = 'FF0000'
    end
  end
end
