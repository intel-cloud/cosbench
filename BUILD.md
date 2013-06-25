  


<!DOCTYPE html>
<html>
  <head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# githubog: http://ogp.me/ns/fb/githubog#">
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>cosbench/BUILD.md at 0.3.2.0 · intel-cloud/cosbench</title>
    <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="GitHub" />
    <link rel="fluid-icon" href="https://github.com/fluidicon.png" title="GitHub" />
    <link rel="apple-touch-icon" sizes="57x57" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="114x114" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="72x72" href="/apple-touch-icon-144.png" />
    <link rel="apple-touch-icon" sizes="144x144" href="/apple-touch-icon-144.png" />
    <link rel="logo" type="image/svg" href="https://github-media-downloads.s3.amazonaws.com/github-logo.svg" />
    <meta property="og:image" content="https://a248.e.akamai.net/assets.github.com/images/modules/logos_page/Octocat.png">
    <link rel="assets" href="https://a248.e.akamai.net/assets.github.com/">
    <link rel="xhr-socket" href="/_sockets" />
    


    <meta name="msapplication-TileImage" content="/windows-tile.png" />
    <meta name="msapplication-TileColor" content="#ffffff" />
    <meta name="selected-link" value="repo_source" data-pjax-transient />
    <meta content="collector.githubapp.com" name="octolytics-host" /><meta content="github" name="octolytics-app-id" /><meta content="3183045" name="octolytics-actor-id" /><meta content="ywang19" name="octolytics-actor-login" /><meta content="ecbca0ec9d2b65c703dcf9ab80049ad7ac31ac8320356bfaeb4e8fa82c95273e" name="octolytics-actor-hash" />

    
    
    <link rel="icon" type="image/x-icon" href="/favicon.ico" />

    <meta content="authenticity_token" name="csrf-param" />
<meta content="zf8j3OXRST40htexlZXaP6TSY++skss1TlvBlRqiHn8=" name="csrf-token" />

    <link href="https://a248.e.akamai.net/assets.github.com/assets/github-da8afabe263d649ce7be36e0f4e768844066f8a4.css" media="all" rel="stylesheet" type="text/css" />
    <link href="https://a248.e.akamai.net/assets.github.com/assets/github2-2754cbe12e8c1e1497ed7ebed2c656fe406187dc.css" media="all" rel="stylesheet" type="text/css" />
    


      <script src="https://a248.e.akamai.net/assets.github.com/assets/frameworks-44b089a6ca2f9a826ab869bed554e38863117420.js" type="text/javascript"></script>
      <script src="https://a248.e.akamai.net/assets.github.com/assets/github-f95f8aaa8ed7687ee7ab69f3f53fe33dc375b9bf.js" type="text/javascript"></script>
      
      <meta http-equiv="x-pjax-version" content="8a3921d0cd627cea5e6fd05be069481f">

        <link data-pjax-transient rel='permalink' href='/intel-cloud/cosbench/blob/4ffd5e60dfad9a27fef472f2edbedaf37b6407dc/BUILD.md'>
    <meta property="og:title" content="cosbench"/>
    <meta property="og:type" content="githubog:gitrepository"/>
    <meta property="og:url" content="https://github.com/intel-cloud/cosbench"/>
    <meta property="og:image" content="https://secure.gravatar.com/avatar/d41d8cd98f00b204e9800998ecf8427e?s=420&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"/>
    <meta property="og:site_name" content="GitHub"/>
    <meta property="og:description" content="cosbench - a benchmark tool for cloud object storage service"/>
    <meta property="twitter:card" content="summary"/>
    <meta property="twitter:site" content="@GitHub">
    <meta property="twitter:title" content="intel-cloud/cosbench"/>

    <meta name="description" content="cosbench - a benchmark tool for cloud object storage service" />


    <meta content="3937236" name="octolytics-dimension-user_id" /><meta content="intel-cloud" name="octolytics-dimension-user_login" /><meta content="8943624" name="octolytics-dimension-repository_id" /><meta content="intel-cloud/cosbench" name="octolytics-dimension-repository_nwo" /><meta content="true" name="octolytics-dimension-repository_public" /><meta content="false" name="octolytics-dimension-repository_is_fork" /><meta content="8943624" name="octolytics-dimension-repository_network_root_id" /><meta content="intel-cloud/cosbench" name="octolytics-dimension-repository_network_root_nwo" />
  <link href="https://github.com/intel-cloud/cosbench/commits/0.3.2.0.atom" rel="alternate" title="Recent Commits to cosbench:0.3.2.0" type="application/atom+xml" />

  </head>


  <body class="logged_in page-blob windows vis-public env-production  ">

    <div class="wrapper">
      
      
      

      <div class="header header-logged-in true">
  <div class="container clearfix">

    <a class="header-logo-invertocat" href="https://github.com/">
  <span class="mega-octicon octicon-mark-github"></span>
</a>

    <div class="divider-vertical"></div>

    
  <a href="/notifications" class="notification-indicator tooltipped downwards" title="You have unread notifications">
    <span class="mail-status unread"></span>
  </a>
  <div class="divider-vertical"></div>


      <div class="command-bar js-command-bar  in-repository">
          <form accept-charset="UTF-8" action="/search" class="command-bar-form" id="top_search_form" method="get">

<input type="text" data-hotkey=" s" name="q" id="js-command-bar-field" placeholder="Search or type a command" tabindex="1" autocapitalize="off"
    data-username="ywang19"
      data-repo="intel-cloud/cosbench"
      data-branch="0.3.2.0"
      data-sha="025cd73f76dce885f42eb01311820eb771ad781f"
  >

    <input type="hidden" name="nwo" value="intel-cloud/cosbench" />

    <div class="select-menu js-menu-container js-select-menu search-context-select-menu">
      <span class="minibutton select-menu-button js-menu-target">
        <span class="js-select-button">This repository</span>
      </span>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container">
        <div class="select-menu-modal">

          <div class="select-menu-item js-navigation-item selected">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" class="js-search-this-repository" name="search_target" value="repository" checked="checked" />
            <div class="select-menu-item-text js-select-button-text">This repository</div>
          </div> <!-- /.select-menu-item -->

          <div class="select-menu-item js-navigation-item">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" name="search_target" value="global" />
            <div class="select-menu-item-text js-select-button-text">All repositories</div>
          </div> <!-- /.select-menu-item -->

        </div>
      </div>
    </div>

  <span class="octicon help tooltipped downwards" title="Show command bar help">
    <span class="octicon octicon-question"></span>
  </span>


  <input type="hidden" name="ref" value="cmdform">

</form>
        <ul class="top-nav">
            <li class="explore"><a href="/explore">Explore</a></li>
            <li><a href="https://gist.github.com">Gist</a></li>
            <li><a href="/blog">Blog</a></li>
          <li><a href="https://help.github.com">Help</a></li>
        </ul>
      </div>

    

  

    <ul id="user-links">
      <li>
        <a href="/ywang19" class="name">
          <img height="20" src="https://secure.gravatar.com/avatar/d41d8cd98f00b204e9800998ecf8427e?s=140&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png" width="20" /> ywang19
        </a>
      </li>

        <li>
          <a href="/new" id="new_repo" class="tooltipped downwards" title="Create a new repo">
            <span class="octicon octicon-repo-create"></span>
          </a>
        </li>

        <li>
          <a href="/settings/profile" id="account_settings"
            class="tooltipped downwards"
            title="Account settings ">
            <span class="octicon octicon-tools"></span>
          </a>
        </li>
        <li>
          <a class="tooltipped downwards" href="/logout" data-method="post" id="logout" title="Sign out">
            <span class="octicon octicon-log-out"></span>
          </a>
        </li>

    </ul>


<div class="js-new-dropdown-contents hidden">
  

<ul class="dropdown-menu">
  <li>
    <a href="/new"><span class="octicon octicon-repo-create"></span> New repository</a>
  </li>
  <li>
    <a href="/organizations/new"><span class="octicon octicon-list-unordered"></span> New organization</a>
  </li>

    <li class="section-title">
      <span title="intel-cloud">This organization</span>
    </li>
    <li>
      <a href="/organizations/intel-cloud/teams/new"><span class="octicon octicon-person-team"></span> New team</a>
    </li>
    <li>
      <a href="/organizations/intel-cloud/repositories/new"><span class="octicon octicon-repo-create"></span> New repository</a>
    </li>


    <li class="section-title">
      <span title="intel-cloud/cosbench">This repository</span>
    </li>
    <li>
      <a href="/intel-cloud/cosbench/issues/new"><span class="octicon octicon-issue-opened"></span> New issue</a>
    </li>
      <li>
        <a href="/intel-cloud/cosbench/settings/collaboration"><span class="octicon octicon-person-add"></span> New collaborator</a>
      </li>
</ul>

</div>


    
  </div>
</div>

      

      




            <div class="global-notices">
      <div class="flash-global">
        <div class="container">
            <a href="/users/ywang19/enable_repository_next?nwo=intel-cloud%2Fcosbench" class="button minibutton flash-action blue" data-method="post">Enable Repository Next</a>

            <h2>Hey there, would you like to enable our new repository design?</h2>
            <p>We&rsquo;ve been working hard making a <a href="https://github.com/blog/1529-repository-next">faster, better repository experience</a> and we&rsquo;d love to share it with you.</p>
        </div>
      </div>
    </div>
    <div class="site hfeed" itemscope itemtype="http://schema.org/WebPage">
      <div class="hentry">
        
        <div class="pagehead repohead instapaper_ignore readability-menu ">
          <div class="container">
            <div class="title-actions-bar">
              

<ul class="pagehead-actions">

    <li class="nspr">
      <a href="/intel-cloud/cosbench/pull/new/0.3.2.0" class="button minibutton btn-pull-request" icon_class="octicon-git-pull-request"><span class="octicon octicon-git-pull-request"></span>Pull Request</a>
    </li>

    <li class="subscription">
      <form accept-charset="UTF-8" action="/notifications/subscribe" data-autosubmit="true" data-remote="true" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="zf8j3OXRST40htexlZXaP6TSY++skss1TlvBlRqiHn8=" /></div>  <input id="repository_id" name="repository_id" type="hidden" value="8943624" />

    <div class="select-menu js-menu-container js-select-menu">
      <span class="minibutton select-menu-button  js-menu-target">
        <span class="js-select-button">
          <span class="octicon octicon-eye-unwatch"></span>
          Unwatch
        </span>
      </span>

      <div class="select-menu-modal-holder">
        <div class="select-menu-modal subscription-menu-modal js-menu-content">
          <div class="select-menu-header">
            <span class="select-menu-title">Notification status</span>
            <span class="octicon octicon-remove-close js-menu-close"></span>
          </div> <!-- /.select-menu-header -->

          <div class="select-menu-list js-navigation-container">

            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input id="do_included" name="do" type="radio" value="included" />
                <h4>Not watching</h4>
                <span class="description">You only receive notifications for discussions in which you participate or are @mentioned.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-eye-watch"></span>
                  Watch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item selected">
              <span class="select-menu-item-icon octicon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input checked="checked" id="do_subscribed" name="do" type="radio" value="subscribed" />
                <h4>Watching</h4>
                <span class="description">You receive notifications for all discussions in this repository.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-eye-unwatch"></span>
                  Unwatch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input id="do_ignore" name="do" type="radio" value="ignore" />
                <h4>Ignoring</h4>
                <span class="description">You do not receive any notifications for discussions in this repository.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-mute"></span>
                  Stop ignoring
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

          </div> <!-- /.select-menu-list -->

        </div> <!-- /.select-menu-modal -->
      </div> <!-- /.select-menu-modal-holder -->
    </div> <!-- /.select-menu -->

</form>
    </li>

    <li class="js-toggler-container js-social-container starring-container ">
      <a href="/intel-cloud/cosbench/unstar" class="minibutton with-count js-toggler-target star-button starred upwards" title="Unstar this repo" data-remote="true" data-method="post" rel="nofollow">
        <span class="octicon octicon-star-delete"></span>
        <span class="text">Unstar</span>
      </a>
      <a href="/intel-cloud/cosbench/star" class="minibutton with-count js-toggler-target star-button unstarred upwards" title="Star this repo" data-remote="true" data-method="post" rel="nofollow">
        <span class="octicon octicon-star"></span>
        <span class="text">Star</span>
      </a>
      <a class="social-count js-social-count" href="/intel-cloud/cosbench/stargazers">15</a>
    </li>

        <li>
          <a href="/intel-cloud/cosbench/fork" class="minibutton with-count js-toggler-target fork-button lighter upwards" title="Fork this repo" rel="facebox nofollow">
            <span class="octicon octicon-git-branch-create"></span>
            <span class="text">Fork</span>
          </a>
          <a href="/intel-cloud/cosbench/network" class="social-count">6</a>
        </li>


</ul>

              <h1 itemscope itemtype="http://data-vocabulary.org/Breadcrumb" class="entry-title public">
                <span class="repo-label"><span>public</span></span>
                <span class="mega-octicon octicon-repo"></span>
                <span class="author vcard">
                  <a href="/intel-cloud" class="url fn" itemprop="url" rel="author">
                  <span itemprop="title">intel-cloud</span>
                  </a></span> /
                <strong><a href="/intel-cloud/cosbench" class="js-current-repository">cosbench</a></strong>
              </h1>
            </div>

            
  <ul class="tabs">
    <li class="pulse-nav"><a href="/intel-cloud/cosbench/pulse" class="js-selected-navigation-item " data-selected-links="pulse /intel-cloud/cosbench/pulse" rel="nofollow"><span class="octicon octicon-pulse"></span></a></li>
    <li><a href="/intel-cloud/cosbench/tree/0.3.2.0" class="js-selected-navigation-item selected" data-selected-links="repo_source repo_downloads repo_commits repo_tags repo_branches /intel-cloud/cosbench/tree/0.3.2.0">Code</a></li>
    <li><a href="/intel-cloud/cosbench/network" class="js-selected-navigation-item " data-selected-links="repo_network /intel-cloud/cosbench/network">Network</a></li>
    <li><a href="/intel-cloud/cosbench/pulls" class="js-selected-navigation-item " data-selected-links="repo_pulls /intel-cloud/cosbench/pulls">Pull Requests <span class='counter'>1</span></a></li>

      <li><a href="/intel-cloud/cosbench/issues" class="js-selected-navigation-item " data-selected-links="repo_issues /intel-cloud/cosbench/issues">Issues <span class='counter'>20</span></a></li>

      <li><a href="/intel-cloud/cosbench/wiki" class="js-selected-navigation-item " data-selected-links="repo_wiki /intel-cloud/cosbench/wiki">Wiki</a></li>


    <li><a href="/intel-cloud/cosbench/graphs" class="js-selected-navigation-item " data-selected-links="repo_graphs repo_contributors /intel-cloud/cosbench/graphs">Graphs</a></li>

      <li>
        <a href="/intel-cloud/cosbench/settings">Settings</a>
      </li>

  </ul>
  
<div class="tabnav">

  <span class="tabnav-right">
    <ul class="tabnav-tabs">
          <li><a href="/intel-cloud/cosbench/tags" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_tags /intel-cloud/cosbench/tags">Tags <span class="counter blank">0</span></a></li>
    </ul>
  </span>

  <div class="tabnav-widget scope">


    <div class="select-menu js-menu-container js-select-menu js-branch-menu">
      <a class="minibutton select-menu-button js-menu-target" data-hotkey="w" data-ref="0.3.2.0">
        <span class="octicon octicon-git-branch"></span>
        <i>branch:</i>
        <span class="js-select-button">0.3.2.0</span>
      </a>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container">

        <div class="select-menu-modal">
          <div class="select-menu-header">
            <span class="select-menu-title">Switch branches/tags</span>
            <span class="octicon octicon-remove-close js-menu-close"></span>
          </div> <!-- /.select-menu-header -->

          <div class="select-menu-filters">
            <div class="select-menu-text-filter">
              <input type="text" id="commitish-filter-field" class="js-filterable-field js-navigation-enable" placeholder="Find or create a branch…">
            </div>
            <div class="select-menu-tabs">
              <ul>
                <li class="select-menu-tab">
                  <a href="#" data-tab-filter="branches" class="js-select-menu-tab">Branches</a>
                </li>
                <li class="select-menu-tab">
                  <a href="#" data-tab-filter="tags" class="js-select-menu-tab">Tags</a>
                </li>
              </ul>
            </div><!-- /.select-menu-tabs -->
          </div><!-- /.select-menu-filters -->

          <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket css-truncate" data-tab-filter="branches">

            <div data-filterable-for="commitish-filter-field" data-filterable-type="substring">

                <div class="select-menu-item js-navigation-item selected">
                  <span class="select-menu-item-icon octicon octicon-check"></span>
                  <a href="/intel-cloud/cosbench/blob/0.3.2.0/BUILD.md" class="js-navigation-open select-menu-item-text js-select-button-text css-truncate-target" data-name="0.3.2.0" rel="nofollow" title="0.3.2.0">0.3.2.0</a>
                </div> <!-- /.select-menu-item -->
                <div class="select-menu-item js-navigation-item ">
                  <span class="select-menu-item-icon octicon octicon-check"></span>
                  <a href="/intel-cloud/cosbench/blob/master/BUILD.md" class="js-navigation-open select-menu-item-text js-select-button-text css-truncate-target" data-name="master" rel="nofollow" title="master">master</a>
                </div> <!-- /.select-menu-item -->
            </div>

              <form accept-charset="UTF-8" action="/intel-cloud/cosbench/branches" class="js-create-branch select-menu-item select-menu-new-item-form js-navigation-item js-new-item-form" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="zf8j3OXRST40htexlZXaP6TSY++skss1TlvBlRqiHn8=" /></div>

                <span class="octicon octicon-git-branch-create select-menu-item-icon"></span>
                <div class="select-menu-item-text">
                  <h4>Create branch: <span class="js-new-item-name"></span></h4>
                  <span class="description">from ‘0.3.2.0’</span>
                </div>
                <input type="hidden" name="name" id="name" class="js-new-item-value">
                <input type="hidden" name="branch" id="branch" value="0.3.2.0" />
                <input type="hidden" name="path" id="branch" value="BUILD.md" />
              </form> <!-- /.select-menu-item -->

          </div> <!-- /.select-menu-list -->


          <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket css-truncate" data-tab-filter="tags">
            <div data-filterable-for="commitish-filter-field" data-filterable-type="substring">

            </div>

            <div class="select-menu-no-results">Nothing to show</div>

          </div> <!-- /.select-menu-list -->

        </div> <!-- /.select-menu-modal -->
      </div> <!-- /.select-menu-modal-holder -->
    </div> <!-- /.select-menu -->

  </div> <!-- /.scope -->

  <ul class="tabnav-tabs">
    <li><a href="/intel-cloud/cosbench/tree/0.3.2.0" class="selected js-selected-navigation-item tabnav-tab" data-selected-links="repo_source /intel-cloud/cosbench/tree/0.3.2.0">Files</a></li>
    <li><a href="/intel-cloud/cosbench/commits/0.3.2.0" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_commits /intel-cloud/cosbench/commits/0.3.2.0">Commits</a></li>
    <li><a href="/intel-cloud/cosbench/branches" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_branches /intel-cloud/cosbench/branches" rel="nofollow">Branches <span class="counter ">2</span></a></li>
  </ul>

</div>

  
  
  


            
          </div>
        </div><!-- /.repohead -->

        <div id="js-repo-pjax-container" class="container context-loader-container" data-pjax-container>
          


<!-- blob contrib key: blob_contributors:v21:0766878f2e587bd22666d7a5860c7710 -->
<!-- blob contrib frag key: views10/v8/blob_contributors:v21:0766878f2e587bd22666d7a5860c7710 -->

<div id="slider">
    <div class="frame-meta">

      <p title="This is a placeholder element" class="js-history-link-replace hidden"></p>

        <a href="/intel-cloud/cosbench/find/0.3.2.0" class="js-slide-to" data-hotkey="t" style="display:none">Show File Finder</a>

        <div class="breadcrumb">
          <span class='repo-root js-repo-root'><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/intel-cloud/cosbench/tree/0.3.2.0" class="js-slide-to" data-branch="0.3.2.0" data-direction="back" itemscope="url"><span itemprop="title">cosbench</span></a></span></span><span class="separator"> / </span><strong class="final-path">BUILD.md</strong> <span class="js-zeroclipboard zeroclipboard-button" data-clipboard-text="BUILD.md" data-copied-hint="copied!" title="copy to clipboard"><span class="octicon octicon-clippy"></span></span>
        </div>


        
  <div class="commit file-history-tease">
    <img class="main-avatar" height="24" src="https://secure.gravatar.com/avatar/d41d8cd98f00b204e9800998ecf8427e?s=140&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png" width="24" />
    <span class="author"><a href="/ywang19" rel="author">ywang19</a></span>
    <time class="js-relative-date" datetime="2013-06-24T19:37:07-07:00" title="2013-06-24 19:37:07">June 24, 2013</time>
    <div class="commit-title">
        <a href="/intel-cloud/cosbench/commit/4ffd5e60dfad9a27fef472f2edbedaf37b6407dc" class="message">Update BUILD.md</a>
    </div>

    <div class="participation">
      <p class="quickstat"><a href="#blob_contributors_box" rel="facebox"><strong>1</strong> contributor</a></p>
      
    </div>
    <div id="blob_contributors_box" style="display:none">
      <h2 class="facebox-header">Users who have contributed to this file</h2>
      <ul class="facebox-user-list">
        <li class="facebox-user-list-item">
          <img height="24" src="https://secure.gravatar.com/avatar/d41d8cd98f00b204e9800998ecf8427e?s=140&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png" width="24" />
          <a href="/ywang19">ywang19</a>
        </li>
      </ul>
    </div>
  </div>


    </div><!-- ./.frame-meta -->

    <div class="frames">
      <div class="frame" data-permalink-url="/intel-cloud/cosbench/blob/4ffd5e60dfad9a27fef472f2edbedaf37b6407dc/BUILD.md" data-title="cosbench/BUILD.md at 0.3.2.0 · intel-cloud/cosbench · GitHub" data-type="blob">

        <div id="files" class="bubble">
          <div class="file">
            <div class="meta">
              <div class="info">
                <span class="icon"><b class="octicon octicon-file-text"></b></span>
                <span class="mode" title="File Mode">file</span>
                  <span>105 lines (68 sloc)</span>
                <span>4.965 kb</span>
              </div>
              <div class="actions">
                <div class="button-group">
                        <a class="minibutton"
                           href="/intel-cloud/cosbench/edit/0.3.2.0/BUILD.md"
                           data-method="post" rel="nofollow" data-hotkey="e">Edit</a>
                  <a href="/intel-cloud/cosbench/raw/0.3.2.0/BUILD.md" class="button minibutton " id="raw-url">Raw</a>
                    <a href="/intel-cloud/cosbench/blame/0.3.2.0/BUILD.md" class="button minibutton ">Blame</a>
                  <a href="/intel-cloud/cosbench/commits/0.3.2.0/BUILD.md" class="button minibutton " rel="nofollow">History</a>
                </div><!-- /.button-group -->
              </div><!-- /.actions -->

            </div>
              
  <div id="readme" class="blob instapaper_body">
    <article class="markdown-body entry-content" itemprop="mainContentOfPage"><h2>
<a name="directory-structure" class="anchor" href="#directory-structure"><span class="octicon octicon-link"></span></a>Directory structure</h2>

<p>${ROOT}</p>

<pre><code>+ adaptor-dev       (a sample project for adaptor development)    
+ dev               (all cosbench plugin projects)
+ dist              (all libraries)
    + main          (the osgi launcher)
    + osgi          (all osgi bundles including osgi framework) 
        + libs      (those from third-parties)
        + plugins   (those from cosbench itself)            
+ version           (major binary versions)
    + 
+ release           (supporting files for release)
  + conf            (system and workload configuration files)
  + javadoc         (java doc files)
  + lib-src         (the corresponding source code for CDDL libraries)
  + licenses        (licenses for third-party libraries)
</code></pre>

<h2>
<a name="overview" class="anchor" href="#overview"><span class="octicon octicon-link"></span></a>Overview</h2>

<p>COSBench is developed with Java* language, it is a modular system based on OSGi* framework, and includes a few OSGi 
plugin projects under "dev" folder.</p>

<h2>
<a name="development-environment" class="anchor" href="#development-environment"><span class="octicon octicon-link"></span></a>Development Environment</h2>

<p>Below are steps to set up development environment in eclipse:
1. download eclipse SDK (Juno) from <a href="http://www.eclipse.org/downloads/">http://www.eclipse.org/downloads/</a></p>

<ol>
<li><p>get cosbench source code tree by git or downloading the whole zip package.</p></li>
<li><p>in eclipse,  "File -&gt; Import ... -&gt; Existeing Projects into Workspace", and select the root directory to the "dev" 
folder in cosbench, then eclipse will recognize and import all plugin projects.</p></li>
<li><p>after imported, there will be error signs on projects, additional library path configuration should take to resolve 
them. "Window -&gt; Preferences -&gt; Plug-in Development -&gt; Target Platform", in "Target definitions", choose the active one,
normally, it's "Running Platform". Selecting "Edit..." button to add required plugins. </p></li>
<li><p>In COSBench, 3 folders under "dist" folder for plugins should be included: "main", "osgi" and "osgi\libs". After added those folders, then apply changes.</p></li>
<li><p>Those error signs should disappear, then the development environment is ready.</p></li>
<li><p>After applied modifications on one project, just generate the plugins by right clicking the project, and select "export... -&gt; Plug-in Development -&gt; Deployable plugins and fragments", and set the "Directory" to "dist\osgi" folder. Then the plugins libary will be placed at "dist\osgi\plugins" folder.</p></li>
<li><p>One script called "pack.cmd" or "pack.sh" could help generate one delivable package by passing it the version number.  </p></li>
</ol><h2>
<a name="debugging-environment" class="anchor" href="#debugging-environment"><span class="octicon octicon-link"></span></a>Debugging Environment</h2>

<p>COSBench includes a few OSGi bundle projects, and there are two major executables, one is COSBench Controller, another is COSBench Driver.
To debug both executables, a few special settings are required.</p>

<ol>
<li>
<p>COSBench Controller</p>

<p>1.1 The major bundle project is "cosbench-web-controller".</p>

<p>1.2 Right click on the project, and select "Run As"\"Run Configuration...".</p>

<p>1.3 In "Run Configuration" window, right click on "OSGi Framework" and select "New" to create an new configuration with name "controller".</p>

<p>1.4 In "Bundles" tab, there are a few parameters can set, the settings depend on the file "release\conf.controller\config.ini" in github repository.</p>

<p>1.5 At the header, make changes as following:
    "Default Start level:" = 8
    "Default Auto-Start:" = true</p>

<p>1.6 At the bundle table, for each bundle, there are two parameters: "Start Level" and "Auto-Start". The information is recorded "osgi.bundles" parameter in config.ini.
    e.g.
    "libs/com.springsource.freemarker-2.3.15.jar@2:start" means the freemarker bundle will be with "Start Level" = 2 and "Auto-Start" = true.</p>

<p>1.7 Some bundles may not set the two parameters, like "com.springsource.apache.coyote". in this case, just let it be. </p>

<p>1.8 After configured bundle part, the next step is to configure "Arguments" tab, here is the settings:
    program arguments: -os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog -console
    vm arguments: -Xms40m -Xmx512m -Declipse.ignoreApp=true -Dosgi.noShutdown=true <strong>-Dosgi.startLevel=8</strong> <strong>-Dcosbench.tomcat.config=c:\controller-tomcat-server.xml</strong></p>

<p>1.9 There are two parameter pairs are added beside default, one is "osgi.startLevel=8" which tells framework the start level, another is "cosbench.tomcat.config" whi
    indicates where to find the tomcat configuration file.</p>

<p>1.10 Making one sub folder called "plugins" in "dist\osgi" folder, and copy "org.eclipse.org-xxx.jar" into "plugins" folder, then refresh all bundle projects, now there should
    no error marks on each project.</p>

<p>1.11 Running the project should see messages as below on eclipse console window:</p>

<pre><code>osgi&gt; Persistence bundle starting...
Persistence bundle started.
----------------------------------------------
!!! Service will listen on web port: 19088 !!!
----------------------------------------------
</code></pre>
</li>
<li>
<p>COSBench Driver</p>

<p>1.1 The major bundle project is "cosbench-driver-web". </p>

<p>1.2 The configuration is similar to that for COSBench Controller, and the bundle dependency information can be extracted from "release\conf.driver\config.ini".</p>
</li>
</ol><p>== END ==</p></article>
  </div>

          </div>
        </div>

        <a href="#jump-to-line" rel="facebox[.linejump]" data-hotkey="l" class="js-jump-to-line" style="display:none">Jump to Line</a>
        <div id="jump-to-line" style="display:none">
          <form accept-charset="UTF-8" class="js-jump-to-line-form">
            <input class="linejump-input js-jump-to-line-field" type="text" placeholder="Jump to line&hellip;">
            <button type="submit" class="button">Go</button>
          </form>
        </div>

      </div>
    </div>
</div>

<div id="js-frame-loading-template" class="frame frame-loading large-loading-area" style="display:none;">
  <img class="js-frame-loading-spinner" src="https://a248.e.akamai.net/assets.github.com/images/spinners/octocat-spinner-128.gif" height="64" width="64">
</div>


        </div>
      </div>
      <div class="modal-backdrop"></div>
    </div>

    </div><!-- /.wrapper -->

      <div class="container">
  <div class="site-footer">
    <ul class="site-footer-links right">
      <li><a href="https://status.github.com/">Status</a></li>
      <li><a href="http://developer.github.com">Developer</a></li>
      <li><a href="http://training.github.com">Training</a></li>
      <li><a href="http://shop.github.com">Shop</a></li>
      <li><a href="/blog">Blog</a></li>
      <li><a href="/about">About</a></li>
    </ul>

    <a href="/">
      <span class="mega-octicon octicon-mark-github"></span>
    </a>

    <ul class="site-footer-links">
      <li>&copy; 2013 <span title="0.07348s from fe16.rs.github.com">GitHub</span>, Inc.</li>
        <li><a href="/site/terms">Terms</a></li>
        <li><a href="/site/privacy">Privacy</a></li>
        <li><a href="/security">Security</a></li>
        <li><a href="/contact">Contact</a></li>
    </ul>
  </div><!-- /.site-footer -->
</div><!-- /.container -->


    <div class="fullscreen-overlay js-fullscreen-overlay" id="fullscreen_overlay">
  <div class="fullscreen-container js-fullscreen-container">
    <div class="textarea-wrap">
      <textarea name="fullscreen-contents" id="fullscreen-contents" class="js-fullscreen-contents" placeholder="" data-suggester="fullscreen_suggester"></textarea>
          <div class="suggester-container">
              <div class="suggester fullscreen-suggester js-navigation-container" id="fullscreen_suggester"
                 data-url="/intel-cloud/cosbench/suggestions/commit">
              </div>
          </div>
    </div>
  </div>
  <div class="fullscreen-sidebar">
    <a href="#" class="exit-fullscreen js-exit-fullscreen tooltipped leftwards" title="Exit Zen Mode">
      <span class="mega-octicon octicon-screen-normal"></span>
    </a>
    <a href="#" class="theme-switcher js-theme-switcher tooltipped leftwards"
      title="Switch themes">
      <span class="octicon octicon-color-mode"></span>
    </a>
  </div>
</div>



    <div id="ajax-error-message" class="flash flash-error">
      <span class="octicon octicon-alert"></span>
      <a href="#" class="octicon octicon-remove-close close ajax-error-dismiss"></a>
      Something went wrong with that request. Please try again.
    </div>

    
    <span id='server_response_time' data-time='0.07393' data-host='fe16'></span>
    
  </body>
</html>

